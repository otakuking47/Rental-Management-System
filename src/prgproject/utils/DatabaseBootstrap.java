package prgproject.utils;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Ensures the database schema and seed data are present at application startup.
 * <p>
 * Runs the SQL script {@code schema_dao_aligned.sql} the first time the app
 * launches against an empty MySQL instance. Skips the work on subsequent
 * launches so existing data is never trampled.
 * <p>
 * The check is intentionally cheap: a single SELECT against
 * {@code information_schema} plus a row-count probe on the {@code admin} table.
 */
public final class DatabaseBootstrap {

    private static final String SCHEMA_FILE = "schema_dao_aligned.sql";
    private static final String DB_NAME     = "property_management";
    // Server-level URL (no schema in path) — used for CREATE DATABASE and the
    // initial existence probe.
    private static final String SERVER_URL  = "jdbc:mysql://localhost:3306/";
    private static final String USER        = "root";
    private static final String PASS        = "toor";

    private DatabaseBootstrap() {}

    /**
     * Idempotent — call once at application startup. If the schema is already
     * loaded, returns silently. If it isn't, loads it from the SQL script and
     * shows a brief confirmation dialog. Surfaces errors via dialog so the
     * Swing UI doesn't silently fail to log in.
     */
    public static void ensureInitialized() {
        try {
            if (alreadyInitialized()) {
                return;
            }
            String sql = loadSchemaSql();
            runScript(sql);
            // Seeded silently — no dialog. Errors below still surface to the user.
        } catch (SchemaNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Could not find " + SCHEMA_FILE + ".\n\n"
                + "Run the application from the project root, or place the SQL file\n"
                + "next to the application jar.",
                "Setup error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database bootstrap failed:\n" + e.getMessage() + "\n\n"
                + "Verify MySQL is running on localhost:3306 with root/toor.",
                "Setup error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Could not read schema file: " + e.getMessage(),
                "Setup error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean alreadyInitialized() {
        // Step 1 — does the database exist?
        try (Connection con = DriverManager.getConnection(SERVER_URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(
                 "SELECT 1 FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?")) {
            ps.setString(1, DB_NAME);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
            }
        } catch (SQLException e) {
            // If we can't even talk to MySQL, surface the error in ensureInitialized().
            // Returning false here causes the bootstrap to attempt the script,
            // which will fail with a clearer message via the same connection path.
            return false;
        }

        // Step 2 — does the admin table exist AND have at least one row?
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM admin")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            // admin table missing → not initialized
            return false;
        }
    }

    private static String loadSchemaSql() throws IOException, SchemaNotFoundException {
        // Prefer the classpath copy (lets us ship the SQL inside a JAR).
        try (InputStream in = DatabaseBootstrap.class.getResourceAsStream("/" + SCHEMA_FILE)) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        // Fall back to working directory — typical when running from the repo root.
        Path p = Path.of(SCHEMA_FILE);
        if (Files.exists(p)) {
            return Files.readString(p, StandardCharsets.UTF_8);
        }
        throw new SchemaNotFoundException();
    }

    private static void runScript(String sql) throws SQLException {
        List<String> statements = splitStatements(sql);
        try (Connection con = DriverManager.getConnection(SERVER_URL, USER, PASS);
             Statement stmt = con.createStatement()) {
            // The script's first statement is "USE property_management;" — make sure
            // the database exists before that runs.
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME
                       + " DEFAULT CHARACTER SET utf8mb4");
            for (String s : statements) {
                stmt.execute(s);
            }
        }
    }

    /**
     * Splits a multi-statement SQL script on semicolons, while respecting
     * {@code '...'}, {@code "..."} string literals and {@code --} line comments.
     * Good enough for the project's hand-written schema; not a full parser.
     */
    static List<String> splitStatements(String sql) {
        // Drop full-line "-- ..." comments first.
        StringBuilder cleaned = new StringBuilder(sql.length());
        for (String line : sql.split("\\R", -1)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) continue;
            cleaned.append(line).append('\n');
        }
        String body = cleaned.toString();

        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false, inDouble = false;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '\\' && i + 1 < body.length() && (inSingle || inDouble)) {
                // SQL escape — copy both chars verbatim.
                cur.append(c).append(body.charAt(++i));
                continue;
            }
            if (c == '\'' && !inDouble) inSingle = !inSingle;
            else if (c == '"' && !inSingle) inDouble = !inDouble;

            if (c == ';' && !inSingle && !inDouble) {
                String stmt = cur.toString().trim();
                if (!stmt.isEmpty()) out.add(stmt);
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) out.add(tail);
        return out;
    }

    private static class SchemaNotFoundException extends Exception {}
}
