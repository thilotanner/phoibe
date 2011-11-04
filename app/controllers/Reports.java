package controllers;

import models.AdditionalReportItem;
import models.MetricProductReportItem;
import models.Order;
import models.OrderStatus;
import models.Report;
import models.ReportItem;
import models.ReportTransition;
import models.ReportType;
import play.i18n.Messages;
import util.reporting.ReportPDFCreator;
import util.transition.TransitionStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Reports extends ApplicationController {
    public static void confirmCreate(Long orderId, Long reportTypeId) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        notFoundIfNull(reportTypeId);
        ReportType reportType = ReportType.findById(reportTypeId);
        notFoundIfNull(reportType);

        render(order, reportType);
    }

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

        order.currentReport = report;
        order.orderStatus = OrderStatus.IN_PROGRESS;
        order.save();

        flash.success(Messages.get("successfullyCreated", report.reportType.name));

        show(report.id);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        if(!report.isCurrentReport()) {
            flash.now("info", Messages.get("report.onlyCurrentReportsAreChangeable"));
        }

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

    public static void confirmTransition(Long id, Long reportTransitionId) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        notFoundIfNull(reportTransitionId);
        ReportTransition reportTransition = ReportTransition.findById(reportTransitionId);
        notFoundIfNull(reportTransition);

        render(report, reportTransition);
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

            flash.success(Messages.get("successfullyCreated", resultReport.reportType.name));

            show(resultReport.id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create transition strategy", e);
        }
    }

    public static void reportItemForm(Long reportItemId) {
        notFoundIfNull(reportItemId);
        ReportItem reportItem = ReportItem.findById(reportItemId);
        notFoundIfNull(reportItem);

        if(reportItem instanceof AdditionalReportItem) {
            AdditionalReportItems.form(reportItemId);
        } else if(reportItem instanceof MetricProductReportItem) {
            MetricProductReportItems.form(reportItemId);
        } else {
            throw new UnsupportedOperationException("Unknown report item class: " + reportItem.getClass().getName());
        }
    }
}
