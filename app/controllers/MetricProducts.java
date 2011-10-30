package controllers;

import models.Metric;
import models.MetricProduct;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class MetricProducts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> metricProducts = Model.Manager.factoryFor(MetricProduct.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(MetricProduct.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(metricProducts, count);
    }

    public static void show(Long id) {
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        render(metricProduct);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            render();
        }

        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);

        render(metricProduct);
    }

    public static void save(@Valid MetricProduct metricProduct) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", metricProduct);
        }

        metricProduct.save();
        flash.success(Messages.get("successfullySaved", Messages.get("metricProduct")));
        index(1, null, null, null);
    }

    private static void initRenderArgs() {
        renderArgs.put("metrics", Metric.all().<Metric>fetch());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
