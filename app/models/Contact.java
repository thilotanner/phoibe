package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Contact extends EnhancedModel {

    public String company;

    public String firstName;

    @Required
    public String lastName;

    public String street;

    public String postalCode;

    @Required
    public String city;

    public String countryCode;

    public String phone;

    public String fax;

    public String mobile;

    @Email
    public String email;

    @URL
    public String website;

    @Lob
    public String comments;

    public String getLabel() {
        return String.format("%s, %s %s, %s", company, firstName, lastName, city);
    }
}
