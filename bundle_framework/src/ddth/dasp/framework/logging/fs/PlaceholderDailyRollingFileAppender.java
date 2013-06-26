package ddth.dasp.framework.logging.fs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;

/**
 * Extends {@link DailyRollingFileAppender} to support file name with place
 * holders.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class PlaceholderDailyRollingFileAppender extends DailyRollingFileAppender {

    private final static Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+?)\\}");

    /**
     * Constructs a new LsDailyRollingFileAppender object.
     */
    public PlaceholderDailyRollingFileAppender() {
    }

    /**
     * Substitutes a string. This method Substitutes the supplied string with an
     * environment variable.
     * 
     * @param str
     *            String
     * @return String
     */
    protected static String subtitute(String str) {
        String[] tokens = str.split("\\s*\\|\\s*");
        for (String token : tokens) {
            String replacement = System.getenv(token);
            if (replacement != null) {
                return replacement;
            }
        }
        return str;
    }

    /**
     * Normalizes file name, supports place holders such as ${CATALINA_HOME}
     * 
     * @param file
     *            String
     * @return String
     */
    protected String normalizeFile(String file) {
        if (StringUtils.isBlank(file)) {
            return "";
        }
        Matcher m = PATTERN.matcher(file);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String subtitution = subtitute(m.group(1));
            m.appendReplacement(sb, Matcher.quoteReplacement(subtitution));
        }
        m.appendTail(sb);
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Log file: " + sb.toString());
        // }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFile(String file) {
        super.setFile(normalizeFile(file));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFile(String file, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        super.setFile(normalizeFile(file), append, bufferedIO, bufferSize);
    }
}
