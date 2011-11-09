package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PrecalculatedProduct extends Product {

    @Required
    @ManyToOne
    public Metric metric;

    @Required
    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;
}
