package ddth.dasp.common.mls;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class CascadeLanguage extends AbstractLanguage {

    private Map<String, ILanguage> languages = new HashMap<String, ILanguage>();

    public CascadeLanguage() {
    }

    public CascadeLanguage(Locale locale, String name) {
        super(locale, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        synchronized (languages) {
            for (Entry<String, ILanguage> entry : languages.entrySet()) {
                ILanguage language = entry.getValue();
                if (language instanceof AbstractLanguage) {
                    ((AbstractLanguage) language).init();
                }
            }
        }
    }

    /**
     * Adds a language pack to the cascading.
     * 
     * @param name
     * @param language
     * @return
     */
    public CascadeLanguage add(String name, ILanguage language) {
        synchronized (languages) {
            languages.put(name, language);
        }
        return this;
    }

    /**
     * Removes a language pack from the cascading by name.
     * 
     * @param name
     * @return
     */
    public CascadeLanguage remove(String name) {
        synchronized (languages) {
            languages.remove(name);
        }
        return this;
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
