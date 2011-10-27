package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Contact;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;
import play.mvc.Controller;

import java.lang.reflect.Type;
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

    public static void search(String search) {
        List<Model> contacts = Model.Manager.factoryFor(Contact.class).fetch(
                0,
                getPageSize(),
                null,
                null,
                new ArrayList<String>(),
                search,
                null
        );

        renderJSON(contacts, new JsonSerializer<Contact>() {

			public JsonElement serialize(Contact contact, Type type,
					JsonSerializationContext jsonSerializationContext)
			{
				JsonObject object = new JsonObject();
				object.addProperty("id", contact.id);
				object.addProperty("label", contact.getLabel());
				return object;
			}
		});
    }
}
