package controllers;

import models.Contact;
import models.MetricProduct;
import play.i18n.Messages;
import search.ElasticSearch;
import search.IndexEvent;

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
}
