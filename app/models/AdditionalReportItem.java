package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class AdditionalReportItem extends ReportItem {

    @Required
    public BigDecimal amount;

    @Required
    @ManyToOne
    public Metric metric;

    @Required
    public String description;

    @Valid
    @Embedded
    public Money pricePerUnit;

    @Required
    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Money getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public ValueAddedTaxRate getValueAddedTaxRate() {
        return valueAddedTaxRate;
    }

    @Override
    public ReportItem duplicate() {
        AdditionalReportItem additionalReportItem = new AdditionalReportItem();
        additionalReportItem.amount = amount;
        additionalReportItem.description = description;
        additionalReportItem.pricePerUnit = pricePerUnit;
        additionalReportItem.metric = metric;
        additionalReportItem.valueAddedTaxRate = valueAddedTaxRate;
        return additionalReportItem;
    }
}
