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
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;
import util.i18n.CurrencyProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MetricProducts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if( Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        Query<MetricProduct> query = ElasticSearch.query(qb, MetricProduct.class);

        query.from((page - 1) * getPageSize()).size(getPageSize());
        query.hydrate(true);

		SearchResults<MetricProduct> results = query.fetch();
        List<MetricProduct> metricProducts = results.objects;

        Long count = results.totalCount;

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

    public static void search(String search) {
        List<Model> metricProducts = Model.Manager.factoryFor(MetricProduct.class).fetch(
                0,
                getPageSize(),
                null,
                null,
                new ArrayList<String>(),
                search,
                null
        );

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

    private static void initRenderArgs() {
        renderArgs.put("metrics", Metric.findAll());
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
        renderArgs.put("valueAddedTaxRates", ValueAddedTaxRate.findAll());
    }
}
