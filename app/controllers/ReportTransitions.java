package controllers;

import models.ReportTransition;
import models.ReportType;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;

public class ReportTransitions extends ApplicationController {
    public static void create(Long reportTypeId) {
        notFoundIfNull(reportTypeId);
        ReportType reportType = ReportType.findById(reportTypeId);
        notFoundIfNull(reportType);

        ReportTransition reportTransition = new ReportTransition();
        reportTransition.reportType = reportType;

        initRenderArgs();
        render("@form", reportTransition);
    }

     public static void form(Long id) {
        if (id == null) {
            render();
        }

        ReportTransition reportTransition = ReportTransition.findById(id);
        notFoundIfNull(reportTransition);

        render(reportTransition);
    }

    public static void save(@Valid ReportTransition reportTransition) {
        if(Validation.hasErrors()) {
            render("@form", reportTransition);
        }

        reportTransition.save();
        flash.success(Messages.get("successfullySaved", Messages.get("reportTransition")));
        ReportTypes.show(reportTransition.reportType.id);
    }

    private static void initRenderArgs() {
        renderArgs.put("reportTypes", ReportType.findAll());
    }
}
