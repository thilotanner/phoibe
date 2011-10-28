package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Account;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.i18n.Messages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Accounts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        List<Model> accounts = Model.Manager.factoryFor(Account.class).fetch(
                (page - 1) * getPageSize(),
                getPageSize(),
                orderBy,
                order,
                new ArrayList<String>(),
                search,
                null
        );

        Long count = Model.Manager.factoryFor(Account.class).count(new ArrayList<String>(), search, null);

        renderArgs.put("pageSize", getPageSize());
        render(accounts, count);
    }

    public static void show(Long id) {
        notFoundIfNull(id);
        Account account = Account.findById(id);
        notFoundIfNull(account);
        render(account);
    }

    public static void form(Long id) {
        if (id == null) {
            render();
        }

        Account account = Account.findById(id);
        notFoundIfNull(account);

        render(account);
    }

    public static void save(@Valid Account account) {
        if(Validation.hasErrors()) {
            render("@form", account);
        }

        account.save();
        flash.success(Messages.get("successfullySaved", Messages.get("account")));
        index(1, null, null, null);
    }

    public static void search(String search) {
        List<Model> contacts = Model.Manager.factoryFor(Account.class).fetch(
                0,
                getPageSize(),
                null,
                null,
                new ArrayList<String>(),
                search,
                null
        );

        renderJSON(contacts, new JsonSerializer<Account>() {

			public JsonElement serialize(Account account, Type type,
					JsonSerializationContext jsonSerializationContext)
			{
				JsonObject object = new JsonObject();
				object.addProperty("id", account.id);
				object.addProperty("label", account.getLabel());
				return object;
			}
		});
    }
}
