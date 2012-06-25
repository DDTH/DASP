package ddth.dasp.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropsUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(PropsUtils.class);

    /**
     * Loads properties from an {@link InputStream}.
     * 
     * The input stream is closed by this method.
     * 
     * @param is
     *            InputStream
     * @return Properties
     */
    public static Properties loadProperties(InputStream is) {
        return loadProperties(is, false);
    }

    /**
     * Loads properties from an {@link InputStream}.
     * 
     * The input stream is closed by this method.
     * 
     * @param is
     *            InputStream
     * @param isXml
     *            boolean
     * @return Properties
     */
    public static Properties loadProperties(InputStream is, boolean isXml) {
        Properties props = new Properties();
        try {
            if (isXml) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return props;
    }
}
