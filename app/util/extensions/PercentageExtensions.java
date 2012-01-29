package util.extensions;

import play.templates.JavaExtensions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PercentageExtensions extends JavaExtensions {
    public static String percentage(Number number) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("###0.0", symbols);
        return String.format("%s%%", decimalFormat.format(number));
    }
}
