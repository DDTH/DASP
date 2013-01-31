package ddth.dasp.common.mls;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CascadeLanguageRegistry implements ILanguageRegistry {

    private final Logger LOGGER = LoggerFactory.getLogger(CascadeLanguageRegistry.class);

    private Map<Locale, CascadeLanguage> languages = new HashMap<Locale, CascadeLanguage>();
    private boolean devMode = false;

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public boolean isDevMode() {
        return devMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(ILanguage language) {
        Locale locale = language.getLocale();
        String name = language.getName();
        if (LOGGER.isDebugEnabled()) {
            String msg = "Registering language pack [" + name + "] with locale [" + locale + "]...";
            LOGGER.debug(msg);
        }
        CascadeLanguage cLanguage = getLanguage(locale);
        if (cLanguage != null) {
            cLanguage.add(name, language);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CascadeLanguage getLanguage(Locale locale) {
        synchronized (languages) {
            CascadeLanguage cLanguage = languages.get(locale);
            if (cLanguage == null) {
                cLanguage = new CascadeLanguage(locale, "");
                try {
                    cLanguage.init();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                languages.put(locale, cLanguage);
            }
            if (devMode) {
                try {
                    cLanguage.init();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return cLanguage;
        }
    }
}
