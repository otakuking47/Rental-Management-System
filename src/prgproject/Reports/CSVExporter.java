package prgproject.Reports;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class CSVExporter implements ReportExporter {

    @Override
    public void export(ReportData data, File destination) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(destination.toPath(), StandardCharsets.UTF_8))) {
            pw.println(escapeRow(data.getColumns()));
            for (List<Object> row : data.getRows()) {
                pw.println(escapeRow(row));
            }
            if (!data.getSummary().isEmpty()) {
                pw.println();
                pw.println(escape("Summary"));
                for (String line : data.getSummary().split("\\R")) {
                    pw.println(escape(line));
                }
            }
        }
    }

    private String escapeRow(List<?> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(values.get(i)));
        }
        return sb.toString();
    }

    private String escape(Object v) {
        String s = v == null ? "" : v.toString();
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }

    @Override public String getDefaultExtension() { return "csv"; }
    @Override public String getFormatName() { return "CSV (Comma-Separated Values)"; }
}
