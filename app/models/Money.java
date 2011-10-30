package models;

import play.data.validation.CheckWith;
import play.data.validation.Required;
import util.check.NumericalCheck;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    public String getRawAmount() {
        if(rawAmount == null && amount != null && currencyCode != null) {
            calculateRawAmount();
        }
        return rawAmount;
    }

    public void setRawAmount(String rawAmount) {
        this.rawAmount = rawAmount;
        if(currencyCode != null && this.rawAmount != null) {
            calculateAmount();
        }
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        if(this.currencyCode != null && rawAmount != null) {
            calculateAmount();
        }
    }

    public String getLabel()
    {
        return String.format("%s %s", getCurrency(currencyCode).getCurrencyCode(), getRawAmount());
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

    private void calculateAmount()
    {
        try {
            double priceValue = Double.parseDouble(rawAmount);
            amount = (long) (priceValue * getConversionFactor());
        } catch (NumberFormatException e) {
            // Do nothing => errors are covered by NumericalCheck.class
        }
    }

    private void calculateRawAmount() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("##.00", symbols);

        rawAmount = decimalFormat.format(amount / getConversionFactor());
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
