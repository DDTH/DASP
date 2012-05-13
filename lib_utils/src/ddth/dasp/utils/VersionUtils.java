package ddth.wfp.utils;

import java.util.regex.Pattern;

public class VersionUtils {
    /**
     * Compares two version strings.
     * 
     * @param v1
     *            String
     * @param v2
     *            String
     * @return int
     */
    public static int compareVersions(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        return s1 != null ? (s2 != null ? s1.compareTo(s2) : 1) : (s2 != null ? -1 : 0);
    }

    /**
     * Normalizes a version string with default separator and max width.
     * 
     * @param version
     *            String
     * @return String
     */
    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    /**
     * Normalizes a version string.
     * 
     * @param version
     *            String
     * @param sep
     *            String separator
     * @param maxWidth
     *            int
     * @return String
     */
    public static String normalisedVersion(String version, String sep, int maxWidth) {
        if (version == null) {
            return null;
        }
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
}
