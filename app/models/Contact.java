package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class Contact extends Model {

    public String firstName;

    @Required
    public String lastName;
}
