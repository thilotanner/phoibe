package models;

import play.data.validation.CheckWith;
import play.data.validation.Required;
import util.check.NumericalCheck;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;

@Embeddable
public class Money {

    public Long value;

    @Required
    public String currencyCode;

    @Required
    @CheckWith(NumericalCheck.class)
    @Transient
    public String rawValue;

    public String getRawValue() {
        if(rawValue == null && value != null && currencyCode != null) {
            calculateRawValue();
        }
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
        if(currencyCode != null && this.rawValue != null) {
            calculateValue();
        }
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        if(this.currencyCode != null && rawValue != null) {
            calculateValue();
        }
    }

    public String getLabel()
    {
        return getRawValue();
    }

    @Override
    public String toString()
    {
        return getLabel();
    }

    public Money add(Money otherMoney)
    {
        checkCurrency(otherMoney);

        Money result = new Money();
        result.currencyCode = currencyCode;
        result.value = value += otherMoney.value;
        return result;
    }

    public Money subtract(Money otherMoney)
    {
        checkCurrency(otherMoney);

        Money result = new Money();
        result.currencyCode = currencyCode;
        result.value = value -= otherMoney.value;
        return result;
    }

    public Money multiply(BigDecimal factor) {
        BigDecimal bigDecimalValue = new BigDecimal(value);
        BigDecimal resultValue = bigDecimalValue.multiply(factor);

        Money result = new Money();
        result.currencyCode = currencyCode;
        result.value = resultValue.longValue();
        return result;
    }

    private void calculateValue()
    {
        try {
            double priceValue = Double.parseDouble(rawValue);
            value = (long) (priceValue * getConversionFactor());
        } catch (NumberFormatException e) {
            // Do nothing => errors are covered by NumericalCheck.class
        }
    }

    private void calculateRawValue() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('\'');

        DecimalFormat decimalFormat = new DecimalFormat(",###.00", symbols);

        rawValue = decimalFormat.format(value / getConversionFactor());
    }

    private double getConversionFactor()
    {
        return Math.pow(10.0, getCurrency(currencyCode).getDefaultFractionDigits());
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
