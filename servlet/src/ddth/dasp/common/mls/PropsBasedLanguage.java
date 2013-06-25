package ddth.dasp.common.mls;

import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This language pack manages language elements using {@link Properties}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class PropsBasedLanguage extends AbstractLanguage {

    private Logger LOGGER = LoggerFactory.getLogger(PropsBasedLanguage.class);

    private Properties languageDef = new Properties();

    public PropsBasedLanguage() {
    }

    public PropsBasedLanguage(Locale locale, String name) {
        super(locale, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        languageDef.clear();
    }

    /**
     * Loads language elements from a {@link Properties}.
     * 
     * Note: this method clears all existing language elements.
     * 
     * @param props
     */
    public void loadLanguage(Properties props) {
        if (props != null) {
            languageDef.clear();
            languageDef.putAll(props);
        }
    }

    /**
     * Adds language elements from a {@link Properties}.
     * 
     * @param props
     */
    public void addLanguage(Properties props) {
        if (props != null) {
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
