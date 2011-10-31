package util.reporting;

import models.Report;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import play.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportPDFCreator {
    private static final String REPORT_ENCODING = "UTF-8";

    private Report report;

	private JasperReport jasperReport;
    private Map<String, Object> parameters;


    public ReportPDFCreator(Report report)
    {
        this.report = report;
        init();
    }

    public ByteArrayOutputStream createPDF()
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        parameters.put("report", report);

        try {
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(report.reportItems));

            //Need to use JRPdfExporter directly to set the encoding parameters!
            //JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            JRPdfExporter exporter = new JRPdfExporter();

            //http://www.jasperassistant.com/forum/topic/44/problem_with_encoding_of_report?p1
            exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, REPORT_ENCODING);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, REPORT_ENCODING);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
            exporter.exportReport();
        } catch (Exception e) {
            Logger.error(e, "Error while creating PDF");
            throw new RuntimeException("Error while creating PDF", e);
        }

        return baos;
    }

    private void init()
	{
    	try {
            JasperDesign jasperDesign =
                    JRXmlLoader.load(new ByteArrayInputStream(report.reportType.template.getBytes()));

            JRDesignStyle normalStyle = new JRDesignStyle();
            normalStyle.setDefault(true);
            normalStyle.setName("TTF_defaultStyle");

            //Need following special encoding! see
            //http://anamasry.wordpress.com/2008/12/23/exporting-pdf-using-jasperreport-in-arabic/
            normalStyle.setPdfEncoding("Identity-H");
            normalStyle.setPdfEmbedded(true);

            //# before printing, call style.setPdfFontName(...)
            //following is the default install path on Ubuntu!
            //normalStyle.setPdfFontName("/usr/share/fonts/truetype/msttcorefonts/Arial.ttf");
            normalStyle.setPdfFontName("fonts/Arial.ttf");

            jasperDesign.addStyle(normalStyle);

            jasperDesign.setDefaultStyle(normalStyle);

            jasperReport = JasperCompileManager.compileReport(jasperDesign);

            parameters = new HashMap<String, Object>();

        } catch (Exception e) {
            Logger.error(e, "Error while init Jasperreports");
            throw new RuntimeException("Error while init Jasperreports", e);
        }
	}
}
