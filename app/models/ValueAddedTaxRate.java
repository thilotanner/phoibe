package models;

import play.data.validation.Required;

import javax.persistence.Entity;
import java.math.BigDecimal;

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
        return String.format("%s %%", rate);
    }
}
