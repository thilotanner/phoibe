package models;

import util.i18n.CurrencyProvider;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Report extends EnhancedModel {

    @ManyToOne
    public ReportType reportType;

    @ManyToOne
    public Order order;

    @OneToMany(mappedBy = "report")
    public List<ReportItem> reportItems;

    public boolean isCurrentReport() {
        return id != null && id.equals(order.currentReport.id);
    }

    public Money getTotalPrice() {
        Money money = new Money();
        money.currencyCode = CurrencyProvider.getDefaultCurrency().getCurrencyCode();
        money.value = 0l;

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTotalPrice());
        }

        return money;
    }

    public Money getTax() {
        Money money = new Money();
        money.currencyCode = CurrencyProvider.getDefaultCurrency().getCurrencyCode();
        money.value = 0l;

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTax());
        }

        return money;
    }

    public Money getTaxedTotalPrice() {
        Money money = new Money();
        money.currencyCode = CurrencyProvider.getDefaultCurrency().getCurrencyCode();
        money.value = 0l;

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTaxedTotalPrice());
        }

        return money;
    }
}
