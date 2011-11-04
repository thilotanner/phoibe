package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MetricProduct extends Product {

    @Required
    @ManyToOne
    public Metric metric;

    @Required
    @Valid
    @Embedded
    public Money pricePerMetric;

    @Required
    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;
}
