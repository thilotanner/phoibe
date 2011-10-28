package controllers;

import util.CurrencyProvider;

import java.util.Currency;
import java.util.List;

public class Currencies extends ApplicationController {
    public static void index() {
        List<Currency> preferredCurrencies = CurrencyProvider.getPreferredCurrencies();
        List<Currency> allCurrencies = CurrencyProvider.getAllCurrencies();
        render(preferredCurrencies, allCurrencies);
    }
}
