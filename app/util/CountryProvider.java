package util;

import com.google.common.collect.ImmutableList;
import play.Play;
import play.i18n.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryProvider {
    private static final String PREFERRED_COUNTRIES_KEY = "country.preferred";

    public static List<Locale> getAllCountries() {
        List<Locale> locales = new ArrayList<Locale>();
        for(String isoCode : Locale.getISOCountries()) {
            locales.add(new Locale(Lang.get(), isoCode));
        }
        return new ImmutableList.Builder<Locale>().addAll(locales).build();
    }

    public static List<Locale> getPreferredCountries() {
        List<Locale> locales = new ArrayList<Locale>();
        for(String isoCode : Play.configuration.getProperty(PREFERRED_COUNTRIES_KEY).split(",")) {
            locales.add(new Locale(Lang.get(), isoCode));
        }
        return new ImmutableList.Builder<Locale>().addAll(locales).build();
    }
}
