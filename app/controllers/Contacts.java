package controllers;

import models.Contact;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> contacts = Model.Manager.factoryFor(Contact.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Contact.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(contacts, count);
    }

    public static void show(Long id) {
        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);
        render(contact);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);

        render(contact);
    }

    public static void save(@Valid Contact contact) {
        if(Validation.hasErrors()) {
            render("@form", contact);
        }

        contact.save();
        flash.success(Messages.get("successfullySaved", Messages.get("contact")));
        index(1, null, null, null);
    }

}
