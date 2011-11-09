package controllers;

import models.PrecalculatedProductReportItem;
import models.Report;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

public class PrecalculatedProductReportItems extends ApplicationController {
    public static void create(Long reportId) {
        notFoundIfNull(reportId);
        Report report = Report.findById(reportId);
        notFoundIfNull(report);

        PrecalculatedProductReportItem precalculatedProductReportItem = new PrecalculatedProductReportItem();
        precalculatedProductReportItem.report = report;

        initRenderArgs();
        render("@form", precalculatedProductReportItem);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        PrecalculatedProductReportItem precalculatedProductReportItem = PrecalculatedProductReportItem.findById(id);
        notFoundIfNull(precalculatedProductReportItem);

        initRenderArgs();
        render(precalculatedProductReportItem);
    }

    public static void save(@Valid PrecalculatedProductReportItem precalculatedProductReportItem) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", precalculatedProductReportItem);
        }

        precalculatedProductReportItem.save();
        flash.success(Messages.get("successfullySaved", Messages.get("precalculatedProductReportItem")));
        Reports.show(precalculatedProductReportItem.report.id);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
