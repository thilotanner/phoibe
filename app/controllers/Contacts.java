package controllers;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Contact;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Http;
import search.ElasticSearch;
import search.Query;
import search.SearchResults;
import util.i18n.CountryProvider;

import java.lang.reflect.Type;
import java.util.List;

public class Contacts extends ApplicationController {
    public static void index(int page, String orderBy, String order, String search) {
        if (page < 1) {
            page = 1;
        }

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        Query<Contact> query = ElasticSearch.query(qb, Contact.class);

        query.from((page - 1) * getPageSize()).size(getPageSize());

        if(!Strings.isNullOrEmpty(orderBy)) {
            SortOrder sortOrder = SortOrder.ASC;
            if(!Strings.isNullOrEmpty(order)) {
                if(order.toLowerCase().equals("desc")) {
                    sortOrder = SortOrder.DESC;
                }
            }

            query.addSort(orderBy, sortOrder);
        }

        query.hydrate(true);

		SearchResults<Contact> results = query.fetch();
        List<Contact> contacts = results.objects;

        Long count = results.totalCount;
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
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if(Strings.isNullOrEmpty(search)) {
            qb.must(QueryBuilders.matchAllQuery());
        } else {
            for(String searchPart : search.split("\\s+")) {
                qb.must(QueryBuilders.queryString(String.format("*%s*", searchPart)).defaultField("_all"));
            }
        }

        Query<Contact> query = ElasticSearch.query(qb, Contact.class);

        query.from(0).size(getPageSize());

        query.hydrate(true);

		SearchResults<Contact> results = query.fetch();
        List<Contact> contacts = results.objects;

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
