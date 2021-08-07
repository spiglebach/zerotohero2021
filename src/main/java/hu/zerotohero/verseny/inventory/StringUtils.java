package hu.zerotohero.verseny.inventory;

public class StringUtils {
    private StringUtils() {
    }

    public static String getValueOrNullIfNullOrBlank(String value) {
        return value == null || value.trim().isEmpty() ? null : value;
    }
}
