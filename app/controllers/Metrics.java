package controllers;

import models.Metric;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Metrics extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> metrics = Model.Manager.factoryFor(Metric.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Metric.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(metrics, count);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        Metric metric = Metric.findById(id);
        notFoundIfNull(metric);

        render(metric);
    }

    public static void save(@Valid Metric metric) {
        if(Validation.hasErrors()) {
            render("@form", metric);
        }

        metric.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("metric")));
        index(1, null, null, null);
    }
}
