package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;
import util.string.StringUtils;

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
        String firstLastName = StringUtils.nonEmptyJoin(new String[] {firstName, lastName}, " ");
        return StringUtils.nonEmptyJoin(new String[] {company, firstLastName, city}, ", ");
    }

    public String getFormattedContact() {
        StringBuilder sb = new StringBuilder();

        if(company != null) {
            sb.append(company).append("\n");
        }

        if(firstName != null || lastName != null) {
            sb.append(StringUtils.nonEmptyJoin(new String[] {firstName, lastName}, " ")).append("\n");
        }

        sb.append(getFormattedAddress());
        return sb.toString();
    }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();

        if(street != null) {
            sb.append(street).append("\n");
        }

        sb.append(StringUtils.nonEmptyJoin(new String[]{postalCode, city}, " "));

        return sb.toString();
    }
}
