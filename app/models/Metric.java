package models;

import play.data.validation.Required;

import javax.persistence.Entity;

@Entity
public class Metric extends EnhancedModel {

    @Required
    public String name;

    @Required
    public String symbol;
}
