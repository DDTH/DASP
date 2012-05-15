package ddth.dasp.servlet.mls;

import java.util.Collection;
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
     * @return String
     */
    public String getName();

    /**
     * Gets the locale associated with the language pack.
     * 
     * @return Locale
     */
    public Locale getLocale();

    /**
     * Retrieves a text message from this language.
     * 
     * @param key
     *            String
     * @return String
     */
    public String getMessage(String key);

    /**
     * Retrieves a text message, replaces the place-holder (if any) by the
     * replacement and returns the result.
     * 
     * @param key
     *            String
     * @param replacement
     *            Object
     * @return String
     */
    public String getMessage(String key, Object replacement);

    /**
     * Retrieves a text message, replaces the place-holders (if any) by the
     * replacements and returns the result.
     * 
     * @param key
     *            String
     * @param replacement1
     *            Object
     * @param replacement2
     *            Object
     * @return String
     */
    public String getMessage(String key, Object replacement1, Object replacement2);

    /**
     * Retrieves a text message, replaces the place-holders (if any) by the
     * replacements and returns the result.
     * 
     * @param key
     *            String
     * @param replacement1
     *            Object
     * @param replacement2
     *            Object
     * @param replacement3
     *            Object
     * @return String
     */
    public String getMessage(String key, Object replacement1, Object replacement2,
            Object replacement3);

    /**
     * Retrieves a text message, replaces the place-holders (if any) by the
     * replacements and returns the result.
     * 
     * @param key
     *            String
     * @param replacements
     *            Object[]
     * @return String
     */
    public String getMessage(String key, Object[] replacements);

    /**
     * Retrieves a text message, replaces the place-holders (if any) by the
     * replacements and returns the result.
     * 
     * @param key
     *            String
     * @param replacements
     *            Collection<?>
     * @return String
     */
    public String getMessage(String key, Collection<?> replacements);
}
