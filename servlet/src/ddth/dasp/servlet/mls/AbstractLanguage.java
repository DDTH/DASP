package ddth.dasp.servlet.mls;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;

public abstract class AbstractLanguage implements ILanguage {

    private Locale locale;
    private String name;

    /**
     * Constructs a new {@link AbstractLanguage} object.
     * 
     * @param locale
     *            Locale
     * @param name
     *            String
     */
    public AbstractLanguage(Locale locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
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
    public String getMessage(String key, Object replacement) {
        String value = getMessage(key);
        return value != null ? MessageFormat.format(value, new Object[] { replacement }) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Object replacement1, Object replacement2) {
        String value = getMessage(key);
        return value != null ? MessageFormat.format(value, new Object[] { replacement1,
                replacement2 }) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Object replacement1, Object replacement2,
            Object replacement3) {
        String value = getMessage(key);
        return value != null ? MessageFormat.format(value, new Object[] { replacement1,
                replacement2, replacement3 }) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Object[] replacements) {
        String value = getMessage(key);
        return value != null ? MessageFormat.format(value, replacements) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String key, Collection<?> replacements) {
        return replacements != null ? getMessage(key, replacements.toArray()) : getMessage(key);
    }
}
