package controllers;

import models.Debitor;
import play.data.validation.Valid;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Debitors extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> debitors = Model.Manager.factoryFor(Debitor.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Debitor.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(debitors, count);
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

    public static void save(@Valid Debitor debitor) {
        if(validation.hasErrors()) {
            render("@form", debitor);
        }

        debitor.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("debitor")));
        index(1, null, null, null);
    }
}
