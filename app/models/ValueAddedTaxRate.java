package models;

import play.data.validation.Required;
import play.i18n.Messages;
import util.extensions.PercentageExtensions;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class ValueAddedTaxRate extends EnhancedModel {

    public static List<ValueAddedTaxRate> findAllWithZeroRate() {
        List<ValueAddedTaxRate> rates = findAll();
        ValueAddedTaxRate zeroRate = new ValueAddedTaxRate();
        zeroRate.rate = BigDecimal.ZERO;
        zeroRate.description = Messages.get("valueAddedTaxRate.zeroRate");
        rates.add(zeroRate);
        return rates;
    }
    
    @Required
    public String description;

    @Required
    public BigDecimal rate;

    public BigDecimal getRateFactor() {
        return rate.divide(new BigDecimal(100));
    }

    public String getLabel() {
        return String.format("%s (%s)", description, toString());
    }
    
    @Override
    public String toString() {
        return PercentageExtensions.percentage(rate);
    }
}
