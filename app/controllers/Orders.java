package controllers;

import models.Order;
import models.ReportType;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Orders extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> orders = Model.Manager.factoryFor(Order.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Order.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(orders, count);
    }

    public static void show(Long id) {
        Order order = Order.findById(id);
        notFoundIfNull(order);
        List<ReportType> rootReportTypes = ReportType.find("rootReportType=true").fetch();
        render(order, rootReportTypes);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        Order order = Order.findById(id);
        notFoundIfNull(order);

        render(order);
    }

    public static void save(@Valid Order order) {
        if(Validation.hasErrors()) {
            render("@form", order);
        }

        order.save();
        flash.success(Messages.get("successfullySaved", Messages.get("contact")));
        index(1, null, null, null);
    }
}
