package controllers;

import models.Order;
import models.Report;
import models.ReportType;

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
}
