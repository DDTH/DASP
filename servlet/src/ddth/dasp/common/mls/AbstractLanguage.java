package ddth.dasp.common.mls;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Abstract language pack.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class AbstractLanguage implements ILanguage {

    private Locale locale;
    private String name;

    public AbstractLanguage() {
    }

    public AbstractLanguage(Locale locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    public void init() throws Exception {
        // EMPTY
    }

    public void destroy() throws Exception {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    public AbstractLanguage setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public AbstractLanguage setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getMessage(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Object... replacements) {
        String value = getMessage(key);
        return value != null ? MessageFormat.format(value, replacements) : null;
    }
}
