package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;
import util.string.NonEmptyStringBuilder;
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
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.append(company).addLine();
        nesb.append(firstName).append(lastName).addLine();
        nesb.append(getFormattedAddress());
        return nesb.toString();
    }

    public String getFormattedAddress() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.append(street).addLine();
        nesb.append(postalCode).append(city);
        return nesb.toString();
    }
}
