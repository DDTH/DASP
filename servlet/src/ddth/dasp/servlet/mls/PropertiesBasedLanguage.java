package ddth.dasp.servlet.mls;

import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesBasedLanguage extends AbstractLanguage {

    private Logger LOGGER = LoggerFactory.getLogger(PropertiesBasedLanguage.class);

    private Properties languageDef = new Properties();

    /**
     * Constructs a new {@link PropertiesBasedLanguage} object.
     * 
     * @param locale
     *            Locale
     * @param name
     *            String
     */
    public PropertiesBasedLanguage(Locale locale, String name) {
        super(locale, name);
    }

    /**
     * Loads language elements from a {@link Properties}
     * 
     * @param props
     *            Properties
     */
    public void loadLanguage(Properties props) {
        if (props != null) {
            languageDef.clear();
            languageDef.putAll(props);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key) {
        String value = languageDef.getProperty(key);
        if (value == null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Language element [" + key + "] not found!");
        }
        return value;
    }
}
