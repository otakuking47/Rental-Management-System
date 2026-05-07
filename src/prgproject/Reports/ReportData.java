package prgproject.Reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportData {

    private final String title;
    private final List<String> columns;
    private final List<List<Object>> rows;
    private final String summary;

    public ReportData(String title, List<String> columns, List<List<Object>> rows, String summary) {
        this.title = title == null ? "Report" : title;
        this.columns = columns == null ? Collections.emptyList() : new ArrayList<>(columns);
        this.rows = rows == null ? Collections.emptyList() : new ArrayList<>(rows);
        this.summary = summary == null ? "" : summary;
    }

    public String getTitle() { return title; }
    public List<String> getColumns() { return columns; }
    public List<List<Object>> getRows() { return rows; }
    public String getSummary() { return summary; }
}
