package controllers;

import models.User;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.List;

public class Users extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> users = Model.Manager.factoryFor(User.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(User.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(users, count);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        User user = User.findById(id);
        notFoundIfNull(user);

        flash.now("info", Messages.get("user.emptyPasswordInfo"));
        render(user);
    }

    public static void save(@Valid User user) {
        if(Validation.hasErrors()) {
            render("@form", user);
        }

        user.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("user")));
        index(1, null, null, null);
    }
}
