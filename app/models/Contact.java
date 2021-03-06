package models;

import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.JPA;
import play.i18n.Messages;
import search.ElasticSearch;
import util.check.ContactNameCheck;
import util.string.NonEmptyStringBuilder;
import util.string.StringUtils;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

@Entity
public class Contact extends EnhancedModel {

    public String company;

    @Enumerated(EnumType.STRING)
    public Title title;

    public String firstName;

    @CheckWith(ContactNameCheck.class)
    public String lastName;

    public String street;

    public String postOfficeBox;

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
        String firstLastName = StringUtils.nonEmptyJoin(" ", firstName, lastName);
        return StringUtils.nonEmptyJoin(", ", company, firstLastName, street, city);
    }

    public String getFormattedContact() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        if(company != null && !company.isEmpty()) {
            nesb.append(company).addLine();
            if(title != null) {
                nesb.append(Messages.get("contact.title." + title));
            }
        } else {
            if(title != null) {
                nesb.append(Messages.get("contact.title." + title)).addLine();
            }
        }
        nesb.append(firstName).append(lastName).addLine();
        nesb.append(getFormattedAddress());
        return nesb.toString();
    }

    public String getFormattedFullContact() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.append(getFormattedContact()).addLine();
        if(phone !=  null && !phone.isEmpty()) {
            nesb.append(Messages.get("contact.phone")).append(": ").append(phone).addLine();
        }

        if(fax !=  null && !fax.isEmpty()) {
            nesb.append(Messages.get("contact.fax")).append(": ").append(fax).addLine();
        }

        if(mobile !=  null && !mobile.isEmpty()) {
            nesb.append(Messages.get("contact.mobile")).append(": ").append(mobile).addLine();
        }

        nesb.append(email).addLine();
        nesb.append(website).addLine();
        return nesb.toString();
    }

    public String getFormattedAddress() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        nesb.append(street).addLine();
        nesb.append(postalCode).append(city);
        return nesb.toString();
    }

    public boolean isDeletable() {
        if(getNumberOfDBReferences(MetricProduct.class, "supplier") > 0l) {
            return false;
        }

        if(getNumberOfDBReferences(Creditor.class, "supplier") > 0l) {
            return false;
        }

        if(getNumberOfDBReferences(Order.class, "orderingContact") > 0l) {
            return false;
        }

        if(getNumberOfDBReferences(Order.class, "shippingContact") > 0l) {
            return false;
        }

        if(getNumberOfDBReferences(Order.class, "billingContact") > 0l) {
            return false;
        }

        return true;
    }

    @Override
    public Contact save() {
        Contact contact = super.save();

        // commit transaction prior to index in order to make entity visible to indexer job
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();

        ElasticSearch.index(contact);
        return contact;
    }

    @Override
    public Contact delete() {
        Contact contact = super.delete();

        // commit transaction prior to index in order to make entity visible to indexer job
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();

        ElasticSearch.remove(contact);
        return contact;
    }
}
