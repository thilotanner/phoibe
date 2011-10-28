package util;

import com.google.common.collect.ImmutableList;
import play.Play;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CurrencyProvider {
    private static final String PREFERRED_CURRENCIES_KEY = "currency.preferred";

    public static List<Currency> getAllCurrencies() {
        Set<Currency> currencies = new HashSet<Currency>();
        Locale[] locales = Locale.getAvailableLocales();

        for(Locale locale : locales) {
            try {
                currencies.add(Currency.getInstance(locale));
            } catch (Exception e) {
                // Locale not found
            }
        }
        return new ImmutableList.Builder<Currency>().addAll(currencies).build();
    }

    public static List<Currency> getPreferredCurrencies() {
        String[] currencyCodes = Play.configuration.getProperty(PREFERRED_CURRENCIES_KEY).split(",");
        Set<Currency> currencies = new HashSet<Currency>();
        for(String currencyCode : currencyCodes) {
            try {
                currencies.add(Currency.getInstance(currencyCode));
            } catch (Exception e) {
                // Currency not found
            }
        }
        return new ImmutableList.Builder<Currency>().addAll(currencies).build();
    }

    public static Currency getDefaultCurrency() {
        String[] currencyCodes = Play.configuration.getProperty(PREFERRED_CURRENCIES_KEY).split(",");
        return Currency.getInstance(currencyCodes[0]);
    }
}
