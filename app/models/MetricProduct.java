package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPA;
import search.ElasticSearch;

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

    @Required
    @ManyToOne
    public Contact supplier;

    @Required
    public BigDecimal priceUnit;

    public String orderNumber;

    @JsonIgnore
    @Lob
    public String comments;

    public String getLabel() {
        if(supplier != null && !supplier.company.isEmpty()) {
            return String.format("%s (%s)", name, supplier.company);
        } else {
            return name;
        }
    }

    public boolean isDeletable() {
        return !isReferenced(MetricProductReportItem.class);
    }

    @Override
    public MetricProduct save() {
        MetricProduct metricProduct = super.save();

        // commit transaction prior to index in order to make entity visible to indexer job
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();

        ElasticSearch.index(metricProduct);
        return metricProduct;
    }

    @Override
    public MetricProduct delete() {
        MetricProduct metricProduct = super.delete();

        // commit transaction prior to index in order to make entity visible to indexer job
        JPA.em().getTransaction().commit();
        JPA.em().getTransaction().begin();

        ElasticSearch.remove(metricProduct);
        return metricProduct;
    }
}
