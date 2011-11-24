package controllers;

import models.ValueAddedTaxRate;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class ValueAddedTaxRates extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> valueAddedTaxRates = Model.Manager.factoryFor(ValueAddedTaxRate.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(ValueAddedTaxRate.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(valueAddedTaxRates, count);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        ValueAddedTaxRate valueAddedTaxRate = ValueAddedTaxRate.findById(id);
        notFoundIfNull(valueAddedTaxRate);

        render(valueAddedTaxRate);
    }

    public static void save(@Valid ValueAddedTaxRate valueAddedTaxRate) {
        if(Validation.hasErrors()) {
            render("@form", valueAddedTaxRate);
        }

        valueAddedTaxRate.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("valueAddedTaxRate")));
        index(1, null, null, null);
    }
}
