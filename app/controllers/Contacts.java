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
import play.mvc.Http;
import util.i18n.CountryProvider;

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
        notFoundIfNull(id);
        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);
        render(contact);
    }

    public static void form(Long id) {
        initRenderArgs();
        if (id == null) {
            render();
        }

        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);

        render(contact);
    }

    public static void modalForm() {
        initRenderArgs();
        render();
    }

    public static void save(@Valid Contact contact) {
        if(Validation.hasErrors()) {
            initRenderArgs();
            render("@form", contact);
        }

        contact.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("contact")));
        index(1, null, null, null);
    }

    public static void modalSave(@Valid Contact contact) {
        if(Validation.hasErrors()) {
            initRenderArgs();

            response.status = Http.StatusCode.BAD_REQUEST;
            render("@modalForm", contact);
        }

        contact.loggedSave(getCurrentUser());
        renderJSON(String.format("{ \"id\" : %s, \"label\" : \"%s\" }", contact.id, contact.getLabel()));
    }

    public static void delete(Long id) {
        notFoundIfNull(id);
        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);
        render(contact);
    }

    public static void destroy(Long id) {
        notFoundIfNull(id);
        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);
        contact.loggedDelete(getCurrentUser());
        flash.success(Messages.get("successfullyDeleted", Messages.get("contact")));
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

    public static void popover(Long id) {
        notFoundIfNull(id);
        Contact contact = Contact.findById(id);
        notFoundIfNull(contact);

        render(contact);
    }

    private static void initRenderArgs() {
        renderArgs.put("preferredCountries", CountryProvider.getPreferredCountries());
        renderArgs.put("allCountries", CountryProvider.getAllCountries());
    }
}
