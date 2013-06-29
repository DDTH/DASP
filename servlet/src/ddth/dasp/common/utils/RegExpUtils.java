package ddth.dasp.common.utils;

/**
 * Regular Expression related utilities.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class RegExpUtils {
    /**
     * Escapes a regular expression replacement string.
     * 
     * @param str
     *            String
     * @return String
     */
    public static String regexpReplacementEscape(String str) {
        return regexpReplacementEscape(str, false);
    }

    /**
     * Escapes a regular expression replacement string.
     * 
     * @param str
     *            String
     * @param preserveNull
     *            boolean indicates that returned value can be null or not
     * @return String
     */
    public static String regexpReplacementEscape(String str, boolean preserveNull) {
        return str != null ? str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$")
                : (preserveNull ? null : "");
    }
}
