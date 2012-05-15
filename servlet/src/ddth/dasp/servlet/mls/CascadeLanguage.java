package ddth.dasp.servlet.mls;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class CascadeLanguage extends AbstractLanguage {

    private Map<String, ILanguage> languages = new HashMap<String, ILanguage>();

    /**
     * Constructs a new {@link CascadeLanguage} object.
     * 
     * @param locale
     *            Locale
     * @param name
     *            String
     */
    public CascadeLanguage(Locale locale, String name) {
        super(locale, name);
    }

    /**
     * Adds a language pack to the cascading.
     * 
     * @param name
     *            String
     * @param language
     *            ILanguage
     */
    public void add(String name, ILanguage language) {
        synchronized (languages) {
            languages.put(name, language);
        }
    }

    /**
     * Removes a language pack from the cascading by name.
     * 
     * @param name
     *            String
     */
    public void remove(String name) {
        synchronized (languages) {
            languages.remove(name);
        }
    }

    @Override
    public String getMessage(String key) {
        synchronized (languages) {
            for (Entry<String, ILanguage> entry : languages.entrySet()) {
                ILanguage language = entry.getValue();
                String value = language.getMessage(key);
                if (value != null) {
                    return value;
                }
            }
            return key;
        }
    }
}
