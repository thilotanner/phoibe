package util.reporting;

import models.Report;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
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


    public ReportPDFCreator(Report report) {
        this.report = report;
        init();
    }

    public ByteArrayOutputStream createPDF() {
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

    private void init() {
        try {
            JasperDesign jasperDesign =
                    JRXmlLoader.load(new ByteArrayInputStream(report.reportType.template.getBytes()));

            jasperReport = JasperCompileManager.compileReport(jasperDesign);
            jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

            parameters = new HashMap<String, Object>();

        } catch (Exception e) {
            Logger.error(e, "Error while init Jasperreports");
            throw new RuntimeException("Error while init Jasperreports", e);
        }
    }
}
