package io.swagger.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class StringUtil {
  /**
   * Check if the given array contains the given value (with case-insensitive comparison).
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
   * Join an array of strings with the given separator.
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
   * @param str     The URI string
   * @return the resulting string
   */
  public static String checkTrailingSlash(String str) {
    return str.endsWith("/") ? str : str + "/";
  }


  /**
   * Check a String for a trailing slash and removes it.
   *
   * @param str     The URI string
   * @return the resulting string
   */
  public static String removeTrailingSlash(String str) { return str.endsWith("/") ? (str.substring(0, str.length() - 1)) : str; }

  public static Boolean isNumeric(String str) {
    if (str == null) return true;
    if (str.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
      return true;
    } else {
      return false;
    }
  }
}