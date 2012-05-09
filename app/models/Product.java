package models;

import play.data.validation.Required;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Product extends EnhancedModel {

    @Required
    public String name;

    @Lob
    public String description;
}
