package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class AdditionalReportItem extends ReportItem {

    @Required
    @ManyToOne
    public Metric metric;

    @Required
    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;

    @Override
    public BigDecimal getPriceUnit() {
        return BigDecimal.ONE;
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

    @Override
    public ValueAddedTaxRate getValueAddedTaxRate() {
        return valueAddedTaxRate;
    }

    @Override
    public ReportItem duplicate() {
        AdditionalReportItem additionalReportItem = new AdditionalReportItem();

        // from ReportItem
        additionalReportItem.amount = amount;
        additionalReportItem.description = description;
        additionalReportItem.retailPricePerMetric = retailPricePerMetric;
        additionalReportItem.position = position;

        // from AdditionalReportItem
        additionalReportItem.metric = metric;
        additionalReportItem.valueAddedTaxRate = valueAddedTaxRate;

        return additionalReportItem;
    }
}
