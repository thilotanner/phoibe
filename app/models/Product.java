package models;

import play.data.validation.Required;
import search.annotations.ElasticSearchSortable;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Product extends EnhancedModel {

    @ElasticSearchSortable
    @Required
    public String name;

    @Lob
    public String description;
}
