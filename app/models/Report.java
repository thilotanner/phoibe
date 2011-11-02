package models;

import com.google.common.collect.ImmutableList;
import util.i18n.CurrencyProvider;
import util.string.NonEmptyStringBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTotalPrice());
        }

        return money;
    }

    public Money getTax() {
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTax());
        }

        return money;
    }

    public Money getTaxedTotalPrice() {
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTaxedTotalPrice());
        }

        return money;
    }

    public List<ValueAddedTaxRate> getUsedValueAddedTaxRates() {
        // collect
        Set<ValueAddedTaxRate> valueAddedTaxRates = new HashSet<ValueAddedTaxRate>();
        for(ReportItem reportItem : reportItems) {
            valueAddedTaxRates.add(reportItem.getValueAddedTaxRate());
        }

        //sort
        ValueAddedTaxRate[] taxList = new ValueAddedTaxRate[valueAddedTaxRates.size()];
        taxList = valueAddedTaxRates.toArray(taxList);
        Arrays.sort(taxList, new Comparator<ValueAddedTaxRate>() {
            public int compare(ValueAddedTaxRate valueAddedTaxRate1,
                               ValueAddedTaxRate valueAddedTaxRate2) {
                return valueAddedTaxRate1.id.compareTo(valueAddedTaxRate2.id);
            }
        });

        // return immutable list
        return new ImmutableList.Builder<ValueAddedTaxRate>().add(taxList).build();
    }

    public String getUsedVatRatesAsString() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            nesb.append(valueAddedTaxRate.toString()).addLine();
        }
        return nesb.toString();
    }

    public Map<ValueAddedTaxRate, Money> getTotalsPerValueAddedTaxRate() {
        Map<ValueAddedTaxRate, Money> result = new HashMap<ValueAddedTaxRate, Money>();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            Money money = new Money(CurrencyProvider.getDefaultCurrency());

            for(ReportItem reportItem : reportItems) {
                if(reportItem.getValueAddedTaxRate().id.equals(valueAddedTaxRate.id)) {
                    money = money.add(reportItem.getTotalPrice());
                }
            }

            result.put(valueAddedTaxRate, money);
        }
        return result;
    }

    public String getTotalsPerVatAsString() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        Map<ValueAddedTaxRate, Money> totals = getTotalsPerValueAddedTaxRate();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            nesb.append(totals.get(valueAddedTaxRate).toString()).addLine();
        }
        return nesb.toString();
    }

    public Map<ValueAddedTaxRate, Money> getTaxPerValueAddedTaxRate() {
        Map<ValueAddedTaxRate, Money> result = new HashMap<ValueAddedTaxRate, Money>();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            Money money = new Money(CurrencyProvider.getDefaultCurrency());

            for(ReportItem reportItem : reportItems) {
                if(reportItem.getValueAddedTaxRate().id.equals(valueAddedTaxRate.id)) {
                    money = money.add(reportItem.getTax());
                }
            }

            result.put(valueAddedTaxRate, money);
        }
        return result;
    }

    public String getTaxPerVatAsString() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        Map<ValueAddedTaxRate, Money> taxes = getTaxPerValueAddedTaxRate();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            nesb.append(taxes.get(valueAddedTaxRate).toString()).addLine();
        }
        return nesb.toString();
    }
}
