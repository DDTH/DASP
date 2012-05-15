package ddth.dasp.servlet.mls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageManager.class);

    private static Map<Locale, CascadeLanguage> languages = new HashMap<Locale, CascadeLanguage>();

    /**
     * Initializing method.
     */
    public static void init() {
        // EMPTY
    }

    /**
     * Destruction method.
     */
    public static void destroy() {
        // EMPTY
    }

    /**
     * Gets all current available language locales.
     * 
     * @return Locales[]
     */
    public Locale[] getAvailableLanguageLocales() {
        synchronized (languages) {
            Locale[] locales = languages.keySet().toArray(new Locale[0]);
            Arrays.sort(locales);
            return locales;
        }
    }

    /**
     * Gets a language pack.
     * 
     * @param locale
     *            Locale
     * @return ILanguage
     */
    public static ILanguage getLanguage(Locale locale) {
        synchronized (languages) {
            CascadeLanguage cLanguage = languages.get(locale);
            if (cLanguage == null) {
                cLanguage = new CascadeLanguage(locale, "");
                languages.put(locale, cLanguage);
            }
            return cLanguage;
        }
    }

    /**
     * Registers a language pack.
     * 
     * @param language
     */
    public static void register(ILanguage language) {
        Locale locale = language.getLocale();
        String name = language.getName();
        if (LOGGER.isDebugEnabled()) {
            String msg = "Registering language pack [" + name + "] with locale [" + locale + "]...";
            LOGGER.debug(msg);
        }
        CascadeLanguage cLanguage = (CascadeLanguage) getLanguage(locale);
        if (cLanguage != null) {
            cLanguage.add(name, language);
        }
    }

    // /**
    // * Registers a language pack.
    // *
    // * @param locale
    // * Locale
    // * @param name
    // * String
    // * @param language
    // * ILanguage
    // */
    // public static void register(Locale locale, String name, ILanguage
    // language) {
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER
    // .debug("Registering language pack [" + name + "] with locale [" + locale
    // + "]...");
    // }
    // CascadeLanguage cLanguage = (CascadeLanguage) getLanguage(locale);
    // cLanguage.add(name, language);
    // }

    /**
     * Unregisters a language pack.
     * 
     * @param language
     */
    public static void unregister(ILanguage language) {
        unregister(language.getLocale(), language.getName());
    }

    /**
     * Unregisters a language pack by locale and name
     * 
     * @param locale
     *            Locale
     * @param name
     *            String
     */
    public static void unregister(Locale locale, String name) {
        if (LOGGER.isDebugEnabled()) {
            String msg = "Unregistering language pack [" + name + "] with locale [" + locale
                    + "]...";
            LOGGER.debug(msg);
        }
        CascadeLanguage cLanguage = (CascadeLanguage) getLanguage(locale);
        cLanguage.remove(name);
    }
}