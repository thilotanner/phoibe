package models;

import com.google.common.collect.ImmutableList;
import play.data.validation.Required;
import play.i18n.Messages;
import util.extensions.PercentageExtensions;
import util.i18n.CurrencyProvider;
import util.string.NonEmptyStringBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Required
    public BigDecimal rebatePercentage;

    @Required
    public String conditions;

    @OneToMany(mappedBy = "report")
    @OrderColumn(name = "position")
    public List<ReportItem> reportItems;

    @Transient
    private List<ValueAddedTaxRate> usedValueAddedTaxRates;

    @Transient
    private HashMap<ValueAddedTaxRate, Money> totalsPerValueAddedTaxRate;
    
    @Transient
    private Map<ValueAddedTaxRate, Money> taxPerValueAddedTaxRate;

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

    public Money getRebate() {
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getRebate(rebatePercentage));
        }

        return money;
    }
    
    public String getChargeTexts() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        
        if(rebatePercentage != null &&
           rebatePercentage.compareTo(BigDecimal.ZERO) != 0) {
            nesb.append(String.format("./. %s %s",
                    PercentageExtensions.percentage(rebatePercentage),
                    Messages.get("report.rebatePercentage")));
            nesb.addLine();
        }

        nesb.append(Messages.get("addValueAddedTax"));
        
        return nesb.toString();
    }

    public String getChargeValues() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();

        if(rebatePercentage != null &&
           rebatePercentage.compareTo(BigDecimal.ZERO) != 0) {
            nesb.append(getRebate().toString());
            nesb.addLine();
        }

        nesb.append(getTax().toString());

        return nesb.toString();
    }
    
    public Money getTax() {
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTax(rebatePercentage));
        }

        return money;
    }

    public Money getTaxedTotalPrice() {
        Money money = new Money(CurrencyProvider.getDefaultCurrency());

        for(ReportItem reportItem : reportItems) {
            money = money.add(reportItem.getTaxedTotalPrice(rebatePercentage));
        }

        return money;
    }

    public List<ValueAddedTaxRate> getUsedValueAddedTaxRates() {
        if(usedValueAddedTaxRates == null) {
        
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
    
            // create immutable list
            usedValueAddedTaxRates = new ImmutableList.Builder<ValueAddedTaxRate>().add(taxList).build();
        }
        
        return usedValueAddedTaxRates;
    }

    public String getUsedVatRatesAsString() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            nesb.append(valueAddedTaxRate.toString()).addLine();
        }
        return nesb.toString();
    }

    public Map<ValueAddedTaxRate, Money> getTotalsPerValueAddedTaxRate() {
        if(totalsPerValueAddedTaxRate == null) {
            totalsPerValueAddedTaxRate = new HashMap<ValueAddedTaxRate, Money>();
            for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
                Money money = new Money(CurrencyProvider.getDefaultCurrency());
    
                for(ReportItem reportItem : reportItems) {
                    if(reportItem.getValueAddedTaxRate().id.equals(valueAddedTaxRate.id)) {
                        money = money.add(reportItem.getTotalPrice(rebatePercentage));
                    }
                }

                totalsPerValueAddedTaxRate.put(valueAddedTaxRate, money);
            }
        }
        
        return totalsPerValueAddedTaxRate;
    }

    public Money getTotalPerValueAddedTaxRate(ValueAddedTaxRate valueAddedTaxRate) {
        if(!getUsedValueAddedTaxRates().contains(valueAddedTaxRate)) {
            throw new IllegalArgumentException("Value added tax rate is not used");
        }

        return getTotalsPerValueAddedTaxRate().get(valueAddedTaxRate);
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
        if(taxPerValueAddedTaxRate == null) {
            taxPerValueAddedTaxRate = new HashMap<ValueAddedTaxRate, Money>();
            for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
                Money money = new Money(CurrencyProvider.getDefaultCurrency());
    
                for(ReportItem reportItem : reportItems) {
                    if(reportItem.getValueAddedTaxRate().id.equals(valueAddedTaxRate.id)) {
                        money = money.add(reportItem.getTax(rebatePercentage));
                    }
                }

                taxPerValueAddedTaxRate.put(valueAddedTaxRate, money);
            }
        }

        return taxPerValueAddedTaxRate;
    }

    public Money getTaxPerValueAddedTaxRate(ValueAddedTaxRate valueAddedTaxRate) {
        if(!getUsedValueAddedTaxRates().contains(valueAddedTaxRate)) {
            throw new IllegalArgumentException("Value added tax rate is not used");
        }

        return getTaxPerValueAddedTaxRate().get(valueAddedTaxRate);
    }

    public String getTaxPerVatAsString() {
        NonEmptyStringBuilder nesb = new NonEmptyStringBuilder();
        Map<ValueAddedTaxRate, Money> taxes = getTaxPerValueAddedTaxRate();
        for(ValueAddedTaxRate valueAddedTaxRate : getUsedValueAddedTaxRates()) {
            nesb.append(taxes.get(valueAddedTaxRate).toString()).addLine();
        }
        return nesb.toString();
    }

    public BigDecimal getValueAddedTaxToTotalPriceRatio() {
        return new BigDecimal(getTax().value).divide(new BigDecimal(getTotalPrice().value), 10, RoundingMode.HALF_UP);
    }

    public boolean isEditable() {
        return order.orderStatus == OrderStatus.IN_PROGRESS &&
               isCurrentReport() &&
               reportType.reportTransitions != null &&
               reportType.reportTransitions.size() > 0;
    }

    public String getLabel() {
        return String.format("%s - %d", reportType.name, id);
    }
}
