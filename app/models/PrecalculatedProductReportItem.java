package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class PrecalculatedProductReportItem extends ReportItem {

    @Required
    @ManyToOne
    public PrecalculatedProduct precalculatedProduct;

    @Required
    @Lob
    public String description;

    @Required
    public BigDecimal amount;

    @Valid
    @Embedded
    public Money pricePerUnit;

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Metric getMetric() {
        return precalculatedProduct.metric;
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
    public ReportItem duplicate() {
        PrecalculatedProductReportItem precalculatedProductReportItem = new PrecalculatedProductReportItem();
        precalculatedProductReportItem.precalculatedProduct = precalculatedProduct;
        precalculatedProductReportItem.amount = amount;
        precalculatedProductReportItem.pricePerUnit = pricePerUnit;
        return precalculatedProductReportItem;
    }

    @Override
    public ValueAddedTaxRate getValueAddedTaxRate() {
        return precalculatedProduct.valueAddedTaxRate;
    }
}
