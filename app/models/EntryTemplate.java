package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EntryTemplate extends EnhancedModel {

    @Required
    public String description;

    @Required
    @ManyToOne
    public Account debit;

    @Required
    @ManyToOne
    public Account credit;
}
