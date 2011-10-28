package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.Required;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.util.Currency;

@Embeddable
public class Money {

    @Required
    public Long amount;

    @Required
    @ManyToOne
    public String currencyCode;

    @Transient
    public String rawPrice;

    public void buildPrice()
    {
        double priceValue = Double.parseDouble(rawPrice);
        amount = (long) (priceValue * getConversionFactor());
    }

    public double getConversionFactor()
    {
        return Math.pow(10.0, getCurrency(currencyCode).getDefaultFractionDigits());
    }

    public String getLabel()
    {
        // return String.format("%s %s", currency.symbol, getShownFormattedPrice());
        return null;
    }

    @Override
    public String toString()
    {
        return getLabel();
    }

    public void add(Money otherMoney)
    {
        checkCurrency(otherMoney);
        amount += otherMoney.amount;
    }

    public void subtract(Money otherMoney)
    {
        checkCurrency(otherMoney);
        amount -= otherMoney.amount;
    }

    private Currency getCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }

    private void checkCurrency(Money otherMoney) {
        if (!currencyCode.equals(otherMoney.currencyCode)) {
            throw new IllegalArgumentException(String.format("Incompatible Currency: %s", otherMoney.currencyCode));
        }
    }
}
