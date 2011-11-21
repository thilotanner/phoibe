package controllers;

import models.Contact;
import models.MetricProduct;
import search.ElasticSearch;

public class Search extends ApplicationController {
    public static void index() {
        for(Contact contact : Contact.<Contact>findAll()) {
            ElasticSearch.index(contact);
        }

        for(MetricProduct metricProduct : MetricProduct.<MetricProduct>findAll()) {
            ElasticSearch.index(metricProduct);
        }
    }

    public static void administration() {
        render();
    }
}
