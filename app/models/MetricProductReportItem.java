package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class MetricProductReportItem extends ReportItem {

    @Required
    @ManyToOne
    public MetricProduct metricProduct;

    @Override
    public BigDecimal getPriceUnit() {
        return metricProduct.priceUnit;
    }

    @Override
    public Metric getMetric() {
        return metricProduct.metric;
    }

    @Override
    public ReportItem duplicate() {
        MetricProductReportItem metricProductReportItem = new MetricProductReportItem();

        // from ReportItem
        metricProductReportItem.amount = amount;
        metricProductReportItem.description = description;
        metricProductReportItem.retailPricePerMetric = retailPricePerMetric;
        metricProductReportItem.position = position;

        // from MetricProductReportItem
        metricProductReportItem.metricProduct = metricProduct;

        return metricProductReportItem;
    }

    @Override
    public ValueAddedTaxRate getValueAddedTaxRate() {
        return metricProduct.valueAddedTaxRate;
    }
}
