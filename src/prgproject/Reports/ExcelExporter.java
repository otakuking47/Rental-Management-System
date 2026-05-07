package prgproject.Reports;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Writes an HTML table with a .xls extension. Excel opens HTML tables natively,
 * preserving headers and cell formatting, with no third-party libraries required.
 */
public class ExcelExporter implements ReportExporter {

    @Override
    public void export(ReportData data, File destination) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(destination.toPath(), StandardCharsets.UTF_8))) {
            pw.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" "
                     + "xmlns:x=\"urn:schemas-microsoft-com:office:excel\">");
            pw.println("<head><meta charset=\"UTF-8\"><title>" + esc(data.getTitle()) + "</title>");
            pw.println("<style>"
                     + "body{font-family:'Segoe UI',Arial,sans-serif;}"
                     + "h2{color:#1e2837;}"
                     + "table{border-collapse:collapse;}"
                     + "th{background:#1e2837;color:white;padding:6px 10px;text-align:left;}"
                     + "td{border:1px solid #b0b0b0;padding:4px 10px;}"
                     + ".summary{margin-top:14px;font-style:italic;color:#444;}"
                     + "</style></head><body>");
            pw.println("<h2>" + esc(data.getTitle()) + "</h2>");
            pw.println("<table>");

            pw.print("<tr>");
            for (String col : data.getColumns()) {
                pw.print("<th>" + esc(col) + "</th>");
            }
            pw.println("</tr>");

            for (List<Object> row : data.getRows()) {
                pw.print("<tr>");
                for (Object v : row) {
                    pw.print("<td>" + esc(v == null ? "" : v.toString()) + "</td>");
                }
                pw.println("</tr>");
            }

            pw.println("</table>");
            if (!data.getSummary().isEmpty()) {
                pw.println("<div class=\"summary\">");
                for (String line : data.getSummary().split("\\R")) {
                    pw.println(esc(line) + "<br/>");
                }
                pw.println("</div>");
            }
            pw.println("</body></html>");
        }
    }

    private String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override public String getDefaultExtension() { return "xls"; }
    @Override public String getFormatName() { return "Excel Workbook"; }
}
