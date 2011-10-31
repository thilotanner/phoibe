package controllers;

import models.ReportType;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class ReportTypes extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> reportTypes = Model.Manager.factoryFor(ReportType.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(ReportType.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(reportTypes, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        ReportType reportType = ReportType.findById(id);
        notFoundIfNull(reportType);
        render(reportType);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        ReportType reportType = ReportType.findById(id);
        notFoundIfNull(reportType);

        render(reportType);
    }

    public static void save(@Valid ReportType reportType) {
        if(Validation.hasErrors()) {
            render("@form", reportType);
        }

        reportType.save();
        flash.success(Messages.get("successfullySaved", Messages.get("reportType")));
        index(1, null, null, null);
    }
}
