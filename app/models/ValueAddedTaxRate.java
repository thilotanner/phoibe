package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Entity
public class ValueAddedTaxRate extends EnhancedModel {

    @Required
    public String description;

    @Required
    public BigDecimal rate;

    public BigDecimal getRateFactor() {
        return rate.divide(new BigDecimal(100));
    }

    @Override
    public String toString() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("###0.0", symbols);
        return String.format("%s %%", decimalFormat.format(rate));
    }
}
