package models;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class AdditionalReportItem extends ReportItem {

    public Double amount;

    public String description;

    @Embedded
    public Money price;


    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Money getPrice() {
        return price;
    }

    @Override
    public ReportItem duplicate() {
        AdditionalReportItem additionalReportItem = new AdditionalReportItem();
        additionalReportItem.amount = amount;
        additionalReportItem.description = description;
        additionalReportItem.price = price;
        return additionalReportItem;
    }
}
