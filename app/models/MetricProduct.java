package models;

import play.data.validation.Required;
import play.data.validation.Valid;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class MetricProduct extends Product {

    @Required
    @ManyToOne
    public Metric metric;

    @Required
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="purchaseValue")),
            @AttributeOverride(name="currencyCode", column = @Column(name="purchaseCurrencyCode"))
    })
    public Money purchasePricePerMetric;

    @Required
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value", column = @Column(name="retailValue")),
            @AttributeOverride(name="currencyCode", column = @Column(name="retailCurrencyCode"))
    })
    public Money retailPricePerMetric;

    @Required
    @ManyToOne
    public ValueAddedTaxRate valueAddedTaxRate;

    @ManyToOne
    public Contact supplier;

    @Required
    public BigDecimal priceUnit;

    public String orderNumber;

    @Lob
    public String comments;
}
