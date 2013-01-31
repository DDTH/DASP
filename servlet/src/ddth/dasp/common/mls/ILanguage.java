package ddth.dasp.common.mls;

import java.util.Locale;

/**
 * Represents a language pack.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface ILanguage {
    /**
     * Gets name of the language pack.
     * 
     * @return
     */
    public String getName();

    /**
     * Gets the locale associated with the language pack.
     * 
     * @return
     */
    public Locale getLocale();

    /**
     * Retrieves a text message from this language.
     * 
     * @param key
     * @return
     */
    public String getMessage(String key);

    /**
     * Retrieves a text message, replaces place-holders (if any) by
     * replacement(s) and returns the result.
     * 
     * @param key
     * @param replacements
     * @return
     */
    public String getMessage(String key, Object... replacements);
}
