package controllers;

import models.MetricProductReportItem;
import models.Report;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

public class MetricProductReportItems extends ApplicationController {
    public static void create(Long reportId) {
        notFoundIfNull(reportId);
        Report report = Report.findById(reportId);
        notFoundIfNull(report);

        MetricProductReportItem metricProductReportItem = new MetricProductReportItem();
        metricProductReportItem.report = report;

        initRenderArgs();
        render("@form", metricProductReportItem);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        MetricProductReportItem metricProductReportItem = MetricProductReportItem.findById(id);
        notFoundIfNull(metricProductReportItem);

        initRenderArgs();
        render(metricProductReportItem);
    }

    public static void save(@Valid MetricProductReportItem metricProductReportItem) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", metricProductReportItem);
        }

        Report report = Report.findById(metricProductReportItem.report.id);
        metricProductReportItem.position = report.reportItems.size();
        
        metricProductReportItem.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("metricProductReportItem")));
        Reports.show(metricProductReportItem.report.id);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
