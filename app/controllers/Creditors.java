package controllers;

import models.Creditor;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import util.i18n.CurrencyProvider;

import java.util.ArrayList;
import java.util.List;

public class Creditors extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> creditors = Model.Manager.factoryFor(Creditor.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Creditor.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(creditors, count);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            render();
        }

        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        render(creditor);
    }

    public static void payForm(Long id) {
        notFoundIfNull(id);
        Creditor creditor = Creditor.findById(id);
        notFoundIfNull(creditor);

        initRenderArgs();
        render(creditor);
    }

    public static void save(@Valid Creditor creditor) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", creditor);
        }

        creditor.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("creditor")));
        index(1, null, null, null);
    }

    private static void initRenderArgs() {
        renderArgs.put("defaultCurrency", CurrencyProvider.getDefaultCurrency());
    }
}
