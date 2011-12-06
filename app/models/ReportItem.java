package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ReportItem extends EnhancedModel {

    @ManyToOne
    public Report report;

    @Required
    public BigDecimal amount;

    @Required
    public String description;

    @Valid
    @Embedded
    public Money retailPricePerMetric;
    
    public Integer position;

    public abstract BigDecimal getPriceUnit();

    public abstract Metric getMetric();

    public abstract ReportItem duplicate();

    public abstract ValueAddedTaxRate getValueAddedTaxRate();

    public Money getTotalPrice() {
        return retailPricePerMetric.multiply(amount.divide(getPriceUnit()));
    }

    public Money getTaxedTotalPrice() {
        return getTotalPrice().add(getTax());
    }

    public Money getTax() {
        return getTotalPrice().multiply(getValueAddedTaxRate().getRateFactor());
    }

}
