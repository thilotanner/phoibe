package controllers;

import util.i18n.CountryProvider;

import java.util.List;
import java.util.Locale;

public class Countries extends ApplicationController {
    public static void index() {
        List<Locale> preferredCountries = CountryProvider.getPreferredCountries();
        List<Locale> allCountries = CountryProvider.getAllCountries();
        render(preferredCountries, allCountries);
    }
}
