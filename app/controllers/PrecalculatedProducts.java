package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Metric;
import models.PrecalculatedProduct;
import models.ValueAddedTaxRate;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrecalculatedProducts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> precalculatedProducts = Model.Manager.factoryFor(PrecalculatedProduct.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(PrecalculatedProduct.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(precalculatedProducts, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        PrecalculatedProduct precalculatedProduct = PrecalculatedProduct.findById(id);
        notFoundIfNull(precalculatedProduct);
        render(precalculatedProduct);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            render();
        }

        PrecalculatedProduct precalculatedProduct = PrecalculatedProduct.findById(id);
        notFoundIfNull(precalculatedProduct);

        render(precalculatedProduct);
    }

    public static void save(@Valid PrecalculatedProduct precalculatedProduct) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", precalculatedProduct);
        }

        precalculatedProduct.save();
        flash.success(Messages.get("successfullySaved", Messages.get("precalculatedProduct")));
        index(1, null, null, null);
    }

    public static void search(String search) {
        List<Model> precalculatedProducts = Model.Manager.factoryFor(PrecalculatedProduct.class).fetch(
                0,
                getPageSize(),
                null,
                null,
                new ArrayList<String>(),
                search,
                null
        );

        renderJSON(precalculatedProducts, new JsonSerializer<PrecalculatedProduct>() {

			public JsonElement serialize(PrecalculatedProduct metricProduct,
                                         Type type,
				                         JsonSerializationContext jsonSerializationContext)
			{
				JsonObject object = new JsonObject();
				object.addProperty("id", metricProduct.id);
				object.addProperty("label", metricProduct.name);
				return object;
			}
		});
    }

    public static void description(Long id) {
        notFoundIfNull(id);
        PrecalculatedProduct precalculatedProduct = PrecalculatedProduct.findById(id);
        notFoundIfNull(precalculatedProduct);
        renderHtml(precalculatedProduct.description);
    }


    private static void initRenderArgs() {
        renderArgs.put("metrics", Metric.findAll());
        renderArgs.put("valueAddedTaxRates", ValueAddedTaxRate.findAll());
    }
}
