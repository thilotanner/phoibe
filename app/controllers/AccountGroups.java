package controllers;

import models.AccountGroup;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class AccountGroups extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> accountGroups = Model.Manager.factoryFor(AccountGroup.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(AccountGroup.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(accountGroups, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        AccountGroup accountGroup = AccountGroup.findById(id);
        notFoundIfNull(accountGroup);
        render(accountGroup);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        AccountGroup accountGroup = AccountGroup.findById(id);
        notFoundIfNull(accountGroup);

        render(accountGroup);
    }

    public static void save(@Valid AccountGroup accountGroup) {
        if(Validation.hasErrors()) {
            render("@form", accountGroup);
        }

        accountGroup.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("accountGroup")));
        index(1, null, null, null);
    }
}
