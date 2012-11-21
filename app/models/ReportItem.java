package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ReportItem extends EnhancedModel {

    @JsonIgnore
    @ManyToOne
    public Report report;

    @Required
    public BigDecimal amount;

    @Required
    @Lob
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

    public Money getTotalPrice(BigDecimal rebatePercentage) {
        if(rebatePercentage != null && !rebatePercentage.equals(BigDecimal.ZERO)) {
            return getTotalPrice().subtract(getRebate(rebatePercentage));
        }
        return getTotalPrice();
    }

    public Money getRebate(BigDecimal rebatePercentage) {
        return getTotalPrice().percentage(rebatePercentage);
    }
    
    public Money getTaxedTotalPrice(BigDecimal rebatePercentage) {
        return getTotalPrice(rebatePercentage).add(getTax(rebatePercentage));
    }

    public Money getTax(BigDecimal rebatePercentage) {
        return getTotalPrice(rebatePercentage).multiply(getValueAddedTaxRate().getRateFactor());
    }
}
