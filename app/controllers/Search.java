package controllers;

import deduplication.ContactDeduplicator;
import models.Contact;
import models.MetricProduct;
import models.Order;
import play.i18n.Messages;
import search.ElasticSearch;

import java.util.List;
import java.util.Map;

public class Search extends ApplicationController {
    public static void index() {
        render();
    }

    public static void administration() {
        render();
    }

    public static void indexContacts() {
        ElasticSearch.deleteIndex(Contact.class);
        
        for(Contact contact : Contact.<Contact>findAll()) {
            ElasticSearch.index(contact);
        }

        flash.success(Messages.get("indexingSuccessfullyStarted", Messages.get("contacts")));
        index();
    }

    public static void indexMetricProducts() {
        ElasticSearch.deleteIndex(MetricProduct.class);

        for(MetricProduct metricProduct : MetricProduct.<MetricProduct>findAll()) {
            ElasticSearch.index(metricProduct);
        }

        flash.success(Messages.get("indexingSuccessfullyStarted", Messages.get("metricProducts")));
        index();
    }

    public static void indexOrders() {
        ElasticSearch.deleteIndex(Order.class);

        for(Order order : Order.<Order>findAll()) {
            ElasticSearch.index(order);
        }

        flash.success(Messages.get("indexingSuccessfullyStarted", Messages.get("orders")));
        index();
    }

    public static void deduplicateContacts() throws Exception {
        ContactDeduplicator deduplicator = new ContactDeduplicator();
        List<Map<Long, Contact>> duplicates = deduplicator.deduplicate();
        render(duplicates);
    }
}
