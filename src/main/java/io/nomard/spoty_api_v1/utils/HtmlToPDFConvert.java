package io.nomard.spoty_api_v1.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;

@Log
public class HtmlToPDFConvert {
    static final private String documentDir = "/var/lib/opencore/static/uploads/documents/";

    public static void safeCloseBufferedReader(BufferedReader bufferedReader) throws Exception {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            throw new Exception("safeCloseBufferedReader  - the method got an error. " + e.getMessage());
        }
    }

    public static String htmlConvertToPdf(String html) {
        OutputStream os = null;
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        var path = documentDir + fileCode + ".pdf";
        try {
            os = new FileOutputStream(path);
            final PdfRendererBuilder pdfBuilder = new PdfRendererBuilder();
            pdfBuilder.useFastMode();
            pdfBuilder.withHtmlContent(html, null);
//            pdfBuilder.useFont(new File(concatPath("times.ttf")), "Times", null, null, false);
            pdfBuilder.toStream(os);
            pdfBuilder.run();
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException _) {
            }
        }
        return path;
    }

    public static String concatPath(String... subPathArr) {
        StringBuilder pathBuilder = new StringBuilder();
        for (String subPath : subPathArr) {
            if (!pathBuilder.toString().endsWith(File.separator)) {
                pathBuilder.append(File.separator);
            }
            pathBuilder.append(subPath);
        }

        return pathBuilder.toString();
    }
}