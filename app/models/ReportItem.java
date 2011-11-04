package models;

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

    public abstract BigDecimal getAmount();

    public abstract Metric getMetric();

    public abstract String getDescription();

    public abstract Money getPricePerUnit();

    public abstract ReportItem duplicate();

    public abstract ValueAddedTaxRate getValueAddedTaxRate();

    public Money getTotalPrice() {
        return getPricePerUnit().multiply(getAmount());
    }

    public Money getTaxedTotalPrice() {
        return getTotalPrice().add(getTax());
    }

    public Money getTax() {
        return getTotalPrice().multiply(getValueAddedTaxRate().getRateFactor());
    }

}
