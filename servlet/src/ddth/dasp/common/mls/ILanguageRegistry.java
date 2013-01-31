package ddth.dasp.common.mls;

import java.util.Locale;

public interface ILanguageRegistry {
    /**
     * Registers a language pack.
     * 
     * @param language
     */
    public void register(ILanguage language);

    /**
     * Gets a language pack.
     * 
     * @param locale
     * @return
     */
    public ILanguage getLanguage(Locale locale);
}
