package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class MetricProduct extends EnhancedModel {

    @Required
    public String name;

    @Lob
    public String description;

    @ManyToOne
    public Metric metric;

    @Valid
    @Embedded
    public Money price;
}
