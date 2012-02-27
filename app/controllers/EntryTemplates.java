package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.EntryTemplate;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EntryTemplates extends ApplicationController {
    public static void index(Long accountingPeriodId, int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> entryTemplates = Model.Manager.factoryFor(EntryTemplate.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(EntryTemplate.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(entryTemplates, count);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        EntryTemplate entryTemplate = EntryTemplate.findById(id);
        notFoundIfNull(entryTemplate);

        render(entryTemplate);
    }

    public static void save(@Valid EntryTemplate entryTemplate) {
        if(Validation.hasErrors()) {
            render("@form", entryTemplate);
        }

        entryTemplate.loggedSave(getCurrentUser());
        flash.success(Messages.get("successfullySaved", Messages.get("entryTemplate")));
        index(null, 1, null, null, null);
    }

    public static void search(String search) {
        List<Model> contacts = Model.Manager.factoryFor(EntryTemplate.class).fetch(
                0,
                getPageSize(),
                null,
                null,
                new ArrayList<String>(),
                search,
                null
        );

        renderJSON(contacts, new JsonSerializer<EntryTemplate>() {

            public JsonElement serialize(EntryTemplate entryTemplate, Type type,
                                         JsonSerializationContext jsonSerializationContext)
            {
                JsonObject object = new JsonObject();
                object.addProperty("id", entryTemplate.id);
                object.addProperty("label", entryTemplate.description);
                return object;
            }
        });
    }
    
    public static void debit(Long entryTemplateId) {
        notFoundIfNull(entryTemplateId);
        EntryTemplate entryTemplate = EntryTemplate.findById(entryTemplateId);
        notFoundIfNull(entryTemplate);

        renderJSON(entryTemplate, new JsonSerializer<EntryTemplate>() {

            public JsonElement serialize(EntryTemplate entryTemplate, Type type,
                                         JsonSerializationContext jsonSerializationContext)
            {
                JsonObject object = new JsonObject();
                object.addProperty("id", entryTemplate.debit.id);
                object.addProperty("label", entryTemplate.debit.getLabel());
                return object;
            }
        });
    }

    public static void credit(Long entryTemplateId) {
        notFoundIfNull(entryTemplateId);
        EntryTemplate entryTemplate = EntryTemplate.findById(entryTemplateId);
        notFoundIfNull(entryTemplate);

        renderJSON(entryTemplate, new JsonSerializer<EntryTemplate>() {

            public JsonElement serialize(EntryTemplate entryTemplate, Type type,
                                         JsonSerializationContext jsonSerializationContext)
            {
                JsonObject object = new JsonObject();
                object.addProperty("id", entryTemplate.credit.id);
                object.addProperty("label", entryTemplate.credit.getLabel());
                return object;
            }
        });
    }
}
