package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import util.check.NumericalCheck;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

@Embeddable
public class Money {

    public Long amount;

    @Required
    public String currencyCode;

    @Required
    @CheckWith(NumericalCheck.class)
    @Transient
    public String rawAmount;

    public void buildPrice()
    {
        double priceValue = Double.parseDouble(rawAmount);
        amount = (long) (priceValue * getConversionFactor());
    }

    public double getConversionFactor()
    {
        return Math.pow(10.0, getCurrency(currencyCode).getDefaultFractionDigits());
    }

    public String getLabel()
    {
        NumberFormat numberFormat = DecimalFormat.getCurrencyInstance(Locale.GERMAN);
        numberFormat.setCurrency(getCurrency(currencyCode));
        return numberFormat.format(amount / getConversionFactor());
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
