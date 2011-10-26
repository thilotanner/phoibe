package controllers;

import models.Contact;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.Controller;

import java.util.List;

public class Contacts extends Controller {
    public static void index() {
        List<Contact> contacts = Contact.findAll();
        render(contacts);
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

        flash.success("Contact successfully saved");
        index();
    }

}
