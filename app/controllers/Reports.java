package controllers;

import models.AdditionalReportItem;
import models.MetricProductReportItem;
import models.Order;
import models.OrderStatus;
import models.Report;
import models.ReportItem;
import models.ReportTransition;
import models.ReportType;
import play.Logger;
import play.Play;
import play.data.binding.As;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Http;
import util.reporting.ReportPDFCreator;
import util.transition.TransitionStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

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

    public synchronized static void create(Long orderId, Long reportTypeId) {
        notFoundIfNull(orderId);
        Order order = Order.findById(orderId);
        notFoundIfNull(order);

        notFoundIfNull(reportTypeId);
        ReportType reportType = ReportType.findById(reportTypeId);
        notFoundIfNull(reportType);

        // sanity check --> order is new
        if(!order.orderStatus.equals(OrderStatus.NEW)) {
            flash.error(Messages.get("order.orderIsInProgress"));
            Orders.show(order.id);
        }
        
        Report report = new Report();
        report.order = order;
        report.reportType = reportType;

        // no rebate
        report.rebatePercentage = BigDecimal.ZERO;

        // use default conditions
        report.conditions = Play.configuration.getProperty("report.defaultConditions");

        report.loggedSave(getCurrentUser());

        order.currentReport = report;
        order.orderStatus = OrderStatus.IN_PROGRESS;
        order.loggedSave(getCurrentUser());

        flash.success(Messages.get("successfullyCreated", report.reportType.name));

        show(report.id);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        if(!report.isEditable()) {
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

        String fileName = String.format("%d.pdf", report.id);

        renderBinary(new ByteArrayInputStream(outputStream.toByteArray()), fileName,"application/pdf", true);
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

    public synchronized static void transition(Long id, Long reportTransitionId) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);

        notFoundIfNull(reportTransitionId);
        ReportTransition reportTransition = ReportTransition.findById(reportTransitionId);
        notFoundIfNull(reportTransition);

        // sanity check --> is transition possible
        if(!reportTransition.reportType.equals(report.order.currentReport.reportType)) {
            flash.error(Messages.get("reportTransition.transitionNotApplicable"));
            show(report.id);
        }

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

    public static void confirmDeleteItem(Long reportItemId) {
        notFoundIfNull(reportItemId);
        ReportItem reportItem = ReportItem.findById(reportItemId);
        notFoundIfNull(reportItem);

        render(reportItem);
    }

    public static void destroyItem(Long reportItemId) {
        notFoundIfNull(reportItemId);
        ReportItem reportItem = ReportItem.findById(reportItemId);
        notFoundIfNull(reportItem);

        Report report = reportItem.report;

        reportItem.loggedDelete(getCurrentUser());

        report.reportItems.remove(reportItem);
        
        // update report item order
        for(int i = 0; i < report.reportItems.size(); i++) {
            reportItem = report.reportItems.get(i);
            reportItem.position = i;
            reportItem.save();
        }
        
        flash.success(Messages.get("successfullyDeleted", Messages.get("reportItem")));
        show(reportItem.report.id);
    }

    public static void reorderItems(@As(",") Long[] reportItemIds) {
        if(reportItemIds.length == 0) {
            error();
        }
        
        // sanity check
        ReportItem reportItem = ReportItem.findById(reportItemIds[0]);
        if(reportItem.report.reportItems.size() != reportItemIds.length) {
            Logger.error("Wrong number of report items for reordering");
            error();
        }
        
        for(int i = 0; i < reportItemIds.length; i++) {
            reportItem = ReportItem.findById(reportItemIds[i]);
            reportItem.position = i;
            reportItem.save();
        }

        ok();
    }
    
    public static void rebatePercentageForm(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);
        render(report);
    }
    
    public static void rebatePercentageSave(@Valid Report report) {
        if(Validation.hasErrors()) {
            response.status = Http.StatusCode.BAD_REQUEST;
            render("@rebatePercentageForm", report);
        }

        report.loggedSave(getCurrentUser());
        show(report.id);
    }

    public static void conditionsForm(Long id) {
        notFoundIfNull(id);
        Report report = Report.findById(id);
        notFoundIfNull(report);
        render(report);
    }

    public static void conditionsSave(@Valid Report report) {
        if(Validation.hasErrors()) {
            response.status = Http.StatusCode.BAD_REQUEST;
            render("@conditionsForm", report);
        }

        report.loggedSave(getCurrentUser());
        show(report.id);
    }
}
