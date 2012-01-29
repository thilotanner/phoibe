package controllers;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Metric;
import models.MetricProduct;
import models.ValueAddedTaxRate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;
import util.i18n.CurrencyProvider;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class MetricProducts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
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

        Query<MetricProduct> query = ElasticSearch.query(qb, MetricProduct.class);

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

        query.hydrate(true);

		SearchResults<MetricProduct> results = query.fetch();
        List<MetricProduct> metricProducts = results.objects;

        Long count = results.totalCount;

        renderArgs.put("pageSize", getPageSize());
        render(metricProducts, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        render(metricProduct);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            MetricProduct metricProduct = new MetricProduct();
            metricProduct.priceUnit = BigDecimal.ONE;
            render(metricProduct);
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

        metricProduct.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("metricProduct")));
        index(1, null, null, null);
    }

    public static void delete(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        render(metricProduct);
    }

    public static void destroy(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);

        try {
            metricProduct.loggedDelete(getCurrentUser());
            flash.success(Messages.get("successfullyDeleted", Messages.get("metricProduct")));
        } catch (Exception e) {
            flash.error(Messages.get("isReferenced", Messages.get("metricProduct")));
        }
        index(1, null, null, null);
    }

    public static void search(String search) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        Query<MetricProduct> query = ElasticSearch.query(qb, MetricProduct.class);

        query.from(0).size(getPageSize() * 2);

        query.hydrate(true);

		SearchResults<MetricProduct> results = query.fetch();
        List<MetricProduct> metricProducts = results.objects;

        renderJSON(metricProducts, new JsonSerializer<MetricProduct>() {

			public JsonElement serialize(MetricProduct metricProduct, Type type,
					JsonSerializationContext jsonSerializationContext)
			{
				JsonObject object = new JsonObject();
				object.addProperty("id", metricProduct.id);
				object.addProperty("label", metricProduct.getLabel());
				return object;
			}
		});
    }

    public static void popover(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        render(metricProduct);
    }

    public static void description(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        renderHtml(metricProduct.description);
    }

    public static void retailPricePerMetric(Long id) {
        notFoundIfNull(id);
        MetricProduct metricProduct = MetricProduct.findById(id);
        notFoundIfNull(metricProduct);
        renderHtml(metricProduct.retailPricePerMetric.toString());
    }

    private static void initRenderArgs() {
        renderArgs.put("metrics", Metric.findAll());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
        renderArgs.put("valueAddedTaxRates", ValueAddedTaxRate.findAll());
    }
}
