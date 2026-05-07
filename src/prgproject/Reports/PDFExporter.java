package prgproject.Reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes a real PDF 1.4 document — single-file, no external libraries.
 * Layout: title at top, then a table with header row + data rows. Pages
 * are split automatically when content overflows the page height.
 */
public class PDFExporter implements ReportExporter {

    private static final float PAGE_W   = 612f;   // US Letter @ 72dpi
    private static final float PAGE_H   = 792f;
    private static final float MARGIN_X = 40f;
    private static final float TOP_Y    = 750f;
    private static final float BOTTOM_Y = 50f;
    private static final float TITLE_FONT  = 16f;
    private static final float HEADER_FONT = 10f;
    private static final float CELL_FONT   = 9f;
    private static final float ROW_HEIGHT  = 14f;

    @Override
    public void export(ReportData data, File destination) throws IOException {
        List<String> pageStreams = layoutPages(data);

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Long> objectOffsets = new ArrayList<>();

        writeAscii(pdf, "%PDF-1.4\n%âãÏÓ\n");

        // ── Object 1: Catalog ────────────────────────────────────────
        objectOffsets.add((long) pdf.size());
        writeAscii(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // ── Object 2: Pages tree ─────────────────────────────────────
        StringBuilder kids = new StringBuilder();
        int firstPageObj = 4; // page objects start at 4 (font is 3)
        for (int i = 0; i < pageStreams.size(); i++) {
            if (i > 0) kids.append(' ');
            kids.append(firstPageObj + i * 2).append(" 0 R");
        }
        objectOffsets.add((long) pdf.size());
        writeAscii(pdf, "2 0 obj\n<< /Type /Pages /Kids [" + kids + "] /Count " + pageStreams.size() + " >>\nendobj\n");

        // ── Object 3: Font (Helvetica) ───────────────────────────────
        objectOffsets.add((long) pdf.size());
        writeAscii(pdf, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

        // ── Pages: each page is two objects (Page + Contents stream) ─
        for (int i = 0; i < pageStreams.size(); i++) {
            int pageObjNo     = firstPageObj + i * 2;
            int contentsObjNo = pageObjNo + 1;

            objectOffsets.add((long) pdf.size());
            writeAscii(pdf, pageObjNo + " 0 obj\n"
                    + "<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 " + PAGE_W + " " + PAGE_H + "] "
                    + "/Contents " + contentsObjNo + " 0 R "
                    + "/Resources << /Font << /F1 3 0 R >> >> "
                    + ">>\nendobj\n");

            byte[] streamBytes = pageStreams.get(i).getBytes(StandardCharsets.ISO_8859_1);
            objectOffsets.add((long) pdf.size());
            writeAscii(pdf, contentsObjNo + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
            pdf.write(streamBytes);
            writeAscii(pdf, "\nendstream\nendobj\n");
        }

        // ── xref table ───────────────────────────────────────────────
        long xrefOffset = pdf.size();
        int totalObjects = 3 + 2 * pageStreams.size();
        writeAscii(pdf, "xref\n0 " + (totalObjects + 1) + "\n");
        writeAscii(pdf, "0000000000 65535 f \n");
        for (long off : objectOffsets) {
            writeAscii(pdf, String.format("%010d 00000 n \n", off));
        }

        // ── trailer ──────────────────────────────────────────────────
        writeAscii(pdf, "trailer\n<< /Size " + (totalObjects + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n");

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(pdf.toByteArray());
        }
    }

    private List<String> layoutPages(ReportData data) {
        List<String> pages = new ArrayList<>();
        int colCount = Math.max(1, data.getColumns().size());
        float usableW = PAGE_W - 2 * MARGIN_X;
        float colW = usableW / colCount;

        int rowsPerPage = (int) Math.floor((TOP_Y - BOTTOM_Y - 60f) / ROW_HEIGHT) - 1;
        if (rowsPerPage < 5) rowsPerPage = 5;

        int totalRows = data.getRows().size();
        int rowsRendered = 0;
        int pageIdx = 1;
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRows / rowsPerPage));

        do {
            StringBuilder s = new StringBuilder();
            s.append("BT\n");
            s.append("/F1 ").append(TITLE_FONT).append(" Tf\n");
            s.append(MARGIN_X).append(' ').append(TOP_Y).append(" Td\n");
            s.append('(').append(escapePdf(data.getTitle())).append(") Tj\n");
            s.append("ET\n");

            float headerY = TOP_Y - 28f;
            s.append("BT\n/F1 ").append(HEADER_FONT).append(" Tf\n");
            for (int c = 0; c < colCount; c++) {
                float x = MARGIN_X + c * colW;
                s.append(x).append(' ').append(headerY).append(" Td\n");
                if (c < data.getColumns().size()) {
                    s.append('(').append(escapePdf(truncate(data.getColumns().get(c), colW))).append(") Tj\n");
                }
                s.append(-x).append(' ').append(-headerY).append(" Td\n");
            }
            s.append("ET\n");

            float headerLineY = headerY - 4f;
            s.append("0.4 w\n");
            s.append(MARGIN_X).append(' ').append(headerLineY).append(" m\n");
            s.append(MARGIN_X + usableW).append(' ').append(headerLineY).append(" l\nS\n");

            float y = headerLineY - ROW_HEIGHT;
            int pageRowEnd = Math.min(rowsRendered + rowsPerPage, totalRows);

            s.append("BT\n/F1 ").append(CELL_FONT).append(" Tf\n");
            for (int r = rowsRendered; r < pageRowEnd; r++) {
                List<Object> row = data.getRows().get(r);
                for (int c = 0; c < colCount; c++) {
                    float x = MARGIN_X + c * colW;
                    s.append(x).append(' ').append(y).append(" Td\n");
                    String cell = c < row.size() && row.get(c) != null ? row.get(c).toString() : "";
                    s.append('(').append(escapePdf(truncate(cell, colW))).append(") Tj\n");
                    s.append(-x).append(' ').append(-y).append(" Td\n");
                }
                y -= ROW_HEIGHT;
            }
            s.append("ET\n");

            // footer
            s.append("BT\n/F1 8 Tf\n");
            s.append(MARGIN_X).append(' ').append(BOTTOM_Y - 20f).append(" Td\n");
            String footer = "Page " + pageIdx + " of " + totalPages
                    + "    James Property Holdings — Rental Management System";
            s.append('(').append(escapePdf(footer)).append(") Tj\nET\n");

            // summary on last page
            if (pageRowEnd >= totalRows && !data.getSummary().isEmpty()) {
                float sumY = y - 10f;
                if (sumY < BOTTOM_Y) sumY = BOTTOM_Y + 10f;
                s.append("BT\n/F1 9 Tf\n");
                s.append(MARGIN_X).append(' ').append(sumY).append(" Td\n");
                s.append('(').append(escapePdf("Summary")).append(") Tj\n");
                s.append(0).append(' ').append(-12f).append(" Td\n");
                for (String line : data.getSummary().split("\\R")) {
                    s.append('(').append(escapePdf(line)).append(") Tj\n");
                    s.append(0).append(' ').append(-11f).append(" Td\n");
                }
                s.append("ET\n");
            }

            pages.add(s.toString());
            rowsRendered = pageRowEnd;
            pageIdx++;
        } while (rowsRendered < totalRows);

        return pages;
    }

    private String truncate(String s, float widthPts) {
        // Helvetica @ 9pt averages ~5pt per glyph. Trim conservatively.
        int max = Math.max(8, (int) (widthPts / 5f) - 2);
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(1, max - 1)) + "…";
    }

    private String escapePdf(String s) {
        if (s == null) return "";
        // Strip/replace non-Latin1 chars (PDF strings here are not Unicode-encoded)
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\' || ch == '(' || ch == ')') {
                out.append('\\').append(ch);
            } else if (ch >= 32 && ch < 256) {
                out.append(ch);
            } else if (ch == '\n') {
                out.append("\\n");
            } else {
                out.append('?');
            }
        }
        return out.toString();
    }

    private void writeAscii(ByteArrayOutputStream out, String s) {
        out.writeBytes(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override public String getDefaultExtension() { return "pdf"; }
    @Override public String getFormatName() { return "PDF Document"; }
}
