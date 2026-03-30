package foxiwhitee.FoxLib.utils.helpers;

import net.minecraft.util.StatCollector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@SuppressWarnings("unused")
public class LocalizationUtils {
    private static final DecimalFormat df;
    private static final String[] POSTFIX = {"", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"};
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');

        df = new DecimalFormat("#,###.##", symbols);
    }

    public static String formatNumber(double number) {
        if (number < 0) return "-" + formatNumber(-number);
        if (number < 1000) return DF.format(number).replace(",", ".");

        int offset = 0;
        double value = number;

        while (value >= 1000 && offset < POSTFIX.length - 1) {
            value /= 1000.0;
            offset++;
        }

        if (value >= 1000) {
            return String.format("%.2e", number).replace(",", ".");
        }

        return DF.format(value).replace(",", ".") + POSTFIX[offset];
    }

    public static String delimitingNumber(double number) {
        return df.format(number);
    }

    public static String localize(String string, Object... objects) {
        boolean hasArguments = objects != null && objects.length != 0;
        return !hasArguments ? StatCollector.translateToLocal(string) : String.format(StatCollector.translateToLocal(string), objects);
    }

    public static String localizeF(String string, Object... objects) {
        Object[] args = new Object[objects.length];
        for (int i = 0; i < args.length; i++) {
            Object obj = objects[i];
            if (obj instanceof Number number) {
                args[i] = formatNumber(number.doubleValue());
            } else {
                args[i] = obj;
            }
        }
        return localize(string, args);
    }

}
