package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Product extends EnhancedModel {

    @Required
    public String name;

    @Lob
    public String description;
}
