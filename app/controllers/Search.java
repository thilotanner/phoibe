package controllers;

import models.Contact;
import search.ElasticSearch;

public class Search extends ApplicationController {
    public static void index() {
        for(Contact contact : Contact.<Contact>findAll()) {
            ElasticSearch.index(contact);
        }
    }

    public static void administration() {
        render();
    }
}
