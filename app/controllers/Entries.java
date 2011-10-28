package controllers;

import models.Entry;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class Entries extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> entries = Model.Manager.factoryFor(Entry.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Entry.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(entries, count);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            render();
        }

        Entry entry = Entry.findById(id);
        notFoundIfNull(entry);

        render(entry);
    }

    public static void save(@Valid Entry entry) {
        entry.amount.buildPrice();
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", entry);
        }

        entry.save();
        flash.success(Messages.get("successfullySaved", Messages.get("entry")));
        index(1, null, null, null);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
