package prgproject.Reports;

import java.io.File;
import java.io.IOException;

public interface ReportExporter {

    void export(ReportData data, File destination) throws IOException;

    String getDefaultExtension();

    String getFormatName();
}
