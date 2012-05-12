package controllers;

import com.google.common.base.Strings;
import models.AccountingPeriod;
import models.Order;
import models.OrderStatus;
import models.ReportType;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;

import java.util.ArrayList;
import java.util.List;

public class Orders extends ApplicationController {
    public static void index(String filter, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        boolean filterApplied = false;
        for(ReportType reportType : ReportType.<ReportType>findAll()) {
            if(reportType.name.equals(filter)) {
                qb.must(QueryBuilders.fieldQuery("currentReport.reportType.id", reportType.id));
                qb.must(QueryBuilders.fieldQuery("orderStatus", OrderStatus.IN_PROGRESS.toString()));
                filterApplied = true;
            }
        }

        if(filter != null) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(filter);
                qb.must(QueryBuilders.fieldQuery("orderStatus", orderStatus.toString()));
                filterApplied = true;
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }


        if(!filterApplied) {
            // exclude all deleted orders
            qb.mustNot(QueryBuilders.fieldQuery("orderStatus", OrderStatus.DELETED.toString()));
        }

        Query<Order> query = ElasticSearch.query(qb, Order.class);

        query.from((page - 1) * getPageSize()).size(getPageSize());

        if(!Strings.isNullOrEmpty(orderBy)) {
            SortOrder sortOrder = SortOrder.ASC;
            if(!Strings.isNullOrEmpty(order)) {
                if(order.toLowerCase().equals("desc")) {
                    sortOrder = SortOrder.DESC;
                }
            }

            query.addSort(orderBy, sortOrder);
        }

        List<Order> orders;
        Long count;

        try {
            SearchResults<Order> results = query.fetch();
            orders = results.objects;
            count = results.totalCount;
        } catch (SearchPhaseExecutionException e) {
            Logger.warn(String.format("Error in search query: %s", search), e);
            flash.now("warning", Messages.get("errorInSearchQuery"));

            orders = new ArrayList<Order>();
            count = 0l;
        }

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
        initRenderArgs();
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
            initRenderArgs();
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

        // sanity check --> order is new or in progress
        if(!order.isEditable()) {
            flash.error(Messages.get("order.orderNotInProgress"));
            show(order.id);
        }

        order.orderStatus = orderStatus;
        order.loggedSave(getCurrentUser());
        if(orderStatus.equals(OrderStatus.FINISHED)) {
            flash.success(Messages.get("successfullyFinished", order.description));
        } else if (orderStatus.equals(OrderStatus.ABORTED)) {
            flash.error(Messages.get("successfullyAborted", order.description));
        }

        show(order.id);
    }
    
    public static void delete(Long id) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);
        render(order);
    }
    
    public static void destroy(Long id) {
        notFoundIfNull(id);
        Order order = Order.findById(id);
        notFoundIfNull(order);
        order.orderStatus = OrderStatus.DELETED;
        order.save();
        
        flash.success(Messages.get("successfullyDeleted", Messages.get("order")));
        index(null, 1, null, null, null);
    }

    private static void initRenderArgs() {
        renderArgs.put("accountingPeriods", AccountingPeriod.findAll());
    }
}
