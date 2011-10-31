package controllers;

import models.Order;
import models.Report;
import models.ReportTransition;
import models.ReportType;
import play.Logger;
import util.reporting.ReportPDFCreator;
import util.transition.TransitionStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Reports extends ApplicationController {
    public static void create(Long orderId, Long reportTypeId) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        notFoundIfNull(reportTypeId);
        ReportType reportType = ReportType.findById(reportTypeId);
        notFoundIfNull(reportType);

        Report report = new Report();
        report.order = order;
        report.reportType = reportType;
        report.save();

        show(report.id);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);
        render(report);
    }

    public static void download(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        ReportPDFCreator reportPDFCreator = new ReportPDFCreator(report);

        ByteArrayOutputStream outputStream = reportPDFCreator.createPDF();

        String fileName = report.id + ".pdf";

        renderBinary(new ByteArrayInputStream(outputStream.toByteArray()), fileName,"application/pdf", false);
    }

    public static void transition(Long id, Long reportTransitionId) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        notFoundIfNull(reportTransitionId);
        ReportTransition reportTransition = ReportTransition.findById(reportTransitionId);
        notFoundIfNull(reportTransition);

        try {
            TransitionStrategy transitionStrategy =
                    (TransitionStrategy) Class.forName(reportTransition.transitionStrategyClassName).newInstance();
            Report resultReport = transitionStrategy.transition(reportTransition, report);
            show(resultReport.id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create transition strategy", e);
        }
    }
}
