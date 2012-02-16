package controllers;

import models.Order;
import models.OrderStatus;
import models.ReportType;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Orders extends ApplicationController {
    public static void index(String filter, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        String where = null;
        for(ReportType reportType : ReportType.<ReportType>findAll()) {
            if(reportType.name.equals(filter)) {
                where = String.format("currentReport.reportType.id = %s AND orderStatus = '%s'", reportType.id, OrderStatus.IN_PROGRESS.toString());
            }
        }
        if(OrderStatus.NEW.toString().equals(filter)) {
            where = String.format("orderStatus = '%s'", filter);
        }
        if(OrderStatus.FINISHED.toString().equals(filter)) {
            where = String.format("orderStatus = '%s'", filter);
        }
        if(OrderStatus.ABORTED.toString().equals(filter)) {
            where = String.format("orderStatus = '%s'", filter);
        }
        
        List<Model> orders = Model.Manager.factoryFor(Order.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                where
        );

        Long count = Model.Manager.factoryFor(Order.class).count(new ArrayList<String>(), search, where);

        List<String> reportTypes = new ArrayList<String>();
        for(ReportType reportType : ReportType.<ReportType>findAll()) {
            reportTypes.add(reportType.name);
        }

        renderArgs.put("pageSize", getPageSize());
        render(orders, count, reportTypes, filter);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);

        if(order.orderStatus == OrderStatus.NEW) {
            renderArgs.put("rootReportTypes", ReportType.find("rootReportType=true").fetch());
        }
        render(order);
    }

    public static void form(Long id) {
        Order order;
        if (id == null) {
            order = new Order();
            order.orderStatus = OrderStatus.NEW;
        } else {
            order = Order.findById(id);
            notFoundIfNull(order);
        }

        render(order);
    }

    public static void save(@Valid Order order) {
        if(Validation.hasErrors()) {
            render("@form", order);
        }

        order.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("order")));
        show(order.id);
    }

    public static void confirmFinish(Long id) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);

        render(order);
    }

    public static void finish(Long id) {
        changeOrderStatus(id, OrderStatus.FINISHED);
    }

    public static void confirmAbort(Long id) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);

        render(order);
    }

    public static void abort(Long id) {
        changeOrderStatus(id, OrderStatus.ABORTED);
    }

    private static void changeOrderStatus(Long id, OrderStatus orderStatus) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);

        order.orderStatus = orderStatus;
        order.loggedSave(getCurrentUser());
        if(orderStatus.equals(OrderStatus.FINISHED)) {
            flash.success(Messages.get("successfullyFinished", order.description));
        } else if (orderStatus.equals(OrderStatus.ABORTED)) {
            flash.error(Messages.get("successfullyAborted", order.description));
        }

        show(order.id);
    }
}
