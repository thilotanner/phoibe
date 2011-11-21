package controllers;

import models.MetricProductReportItem;
import models.Report;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;

public class MetricProductReportItems extends ApplicationController {
    public static void create(Long reportId) {
        notFoundIfNull(reportId);
        Report report = Report.findById(reportId);
        notFoundIfNull(report);

        MetricProductReportItem metricProductReportItem = new MetricProductReportItem();
        metricProductReportItem.report = report;

        render("@form", metricProductReportItem);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        MetricProductReportItem metricProductReportItem = MetricProductReportItem.findById(id);
        notFoundIfNull(metricProductReportItem);
        render(metricProductReportItem);
    }

    public static void save(@Valid MetricProductReportItem metricProductReportItem) {
        if(Validation.hasErrors()) {
            render("@form", metricProductReportItem);
        }

        metricProductReportItem.save();
        flash.success(Messages.get("successfullySaved", Messages.get("metricProductReportItem")));
        Reports.show(metricProductReportItem.report.id);
    }
}
