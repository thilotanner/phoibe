package controllers;

import com.google.common.base.Strings;
import models.Debitor;
import models.DebitorStatus;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import play.data.validation.Valid;
import play.db.Model;
import play.i18n.Messages;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;

import java.util.ArrayList;
import java.util.List;

public class Debitors extends ApplicationController {
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

        if(filter != null && !filter.isEmpty()) {
            try {
                DebitorStatus debitorStatus = DebitorStatus.valueOf(filter);
                qb.must(QueryBuilders.fieldQuery("debitorStatus", debitorStatus.toString()));
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }

        Query<Debitor> query = ElasticSearch.query(qb, Debitor.class);

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

        List<Debitor> debitors;
        Long count;

        try {
            SearchResults<Debitor> results = query.fetch();
            debitors = results.objects;
            count = results.totalCount;
        } catch (SearchPhaseExecutionException e) {
            Logger.warn(String.format("Error in search query: %s", search), e);
            flash.now("warning", Messages.get("errorInSearchQuery"));

            debitors = new ArrayList<Debitor>();
            count = 0l;
        }

        renderArgs.put("pageSize", getPageSize());
        render(debitors, count, filter);
    }
    
    public static void show(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);
        render(debitor);
    }

    public static void form(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);
        render(debitor);
    }

    public synchronized static void save(@Valid Debitor debitor) {
        sanityCheck(debitor);
        
        if(validation.hasErrors()) {
            render("@form", debitor);
        }
        
        debitor.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("debitor")));
        index(DebitorStatus.DUE.toString(), 1, null, null, null);
    }

    public static void confirmDiscountAmountDue(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);
        
        render(debitor);
    }
    
    public synchronized static void discountAmountDue(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);

        sanityCheck(debitor);
        
        debitor.buildAndSaveDiscountEntries();

        closeDebitor(debitor, "debitor.successfullyDiscounted");
    }

    public static void confirmChargeOffAmountDue(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);

        render(debitor);
    }

    public synchronized static void chargeOffAmountDue(Long id) {
        notFoundIfNull(id);
        Debitor debitor = Debitor.findById(id);
        notFoundIfNull(debitor);

        sanityCheck(debitor);
        
        debitor.buildAndSaveChargeOffEntries();

        closeDebitor(debitor, "debitor.successfullyChargedOff");
    }
    
    private static void closeDebitor(Debitor debitor, String messageKey) {
        debitor.close();

        flash.success(Messages.get(messageKey));
        show(debitor.id);
    }
    
    protected static void sanityCheck(Debitor debitor) {
        // sanity check --> is debitor still due
        if(!debitor.isEditable()) {
            flash.error(Messages.get("debitor.debitorIsPaid"));
            show(debitor.id);
        }
    }
}
