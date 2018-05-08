package io.swagger.api;


import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Diverse String Helper-Utilities
 */
public class StringUtil {
    /**
     * Check if an array contains a specific value (with case-insensitive comparison).
     *
     * @param array The array
     * @param value The value to search
     * @return true if the array contains the value
     */
    public static boolean containsIgnoreCase(String[] array, String value) {
        for (String str : array) {
            if (value == null && str == null) return true;
            if (value != null && value.equalsIgnoreCase(str)) return true;
        }
        return false;
    }

    /**
     * Join an array of strings with a separator.
     * <p>
     * Note: This might be replaced by utility method from commons-lang or guava someday
     * if one of those libraries is added as dependency.
     * </p>
     *
     * @param array     The array of strings
     * @param separator The separator
     * @return the resulting string
     */
    public static String join(String[] array, String separator) {
        int len = array.length;
        if (len == 0) return "";

        StringBuilder out = new StringBuilder();
        out.append(array[0]);
        for (int i = 1; i < len; i++) {
            out.append(separator).append(array[i]);
        }
        return out.toString();
    }

    /**
     * Check a String for a trailing slash and adds a slash if there is none
     *
     * @param str The URI string
     * @return the resulting string
     */
    public static String checkTrailingSlash(String str) {
        return str.endsWith("/") ? str : str + "/";
    }


    /**
     * Check a String for trailing slash and removes it.
     *
     * @param str the URI string
     * @return the resulting string
     */
    public static String removeTrailingSlash(String str) {
        return str.endsWith("/") ? (str.substring(0, str.length() - 1)) : str;
    }

    /**
     * Check if a string is not null and a valid number
     *
     * @param str String to compare
     * @return Boolean true or false
     */
    public static Boolean isNumeric(String str) {
        return str == null || NumberUtils.isCreatable(str);
    }

    /**
     * Check if a String is an URI.
     *
     * @param uriString The URI string
     * @return Boolean true or false
     */
    public static Boolean isUri(String uriString) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(uriString);
    }
}