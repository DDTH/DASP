package ddth.dasp.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A replacement for java.utils.Properties with enhanced functionality.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class EhProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private int rendering = 0;

    private final static Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * Constructs a new EhProperties object.
     */
    public EhProperties() {
    }

    /**
     * Constructs a new EhProperties with initial values
     * 
     * @param initialValues
     *            Properties
     */
    public EhProperties(Properties initialValues) {
        populateInitialValues(initialValues);
    }

    /**
     * Retrieves a property as a boolean.
     * 
     * @param key
     *            String
     * @return boolean
     */
    public boolean getPropertyAsBoolean(String key) {
        return getPropertyAsBoolean(key, false);
    }

    /**
     * Retrieves a property as a boolean.
     * 
     * @param key
     *            String
     * @param defaultValue
     *            boolean
     * @return boolean
     */
    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null)
            return defaultValue;
        value = value.trim();

        // special cases (English!)
        if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("y"))
            return true;
        if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false")
                || value.equalsIgnoreCase("n") || value.equalsIgnoreCase("f"))
            return false;
        try {
            return Double.parseDouble(value) != 0;
        } catch (Exception e1) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e2) {
                return defaultValue;
            }
        }
    }

    /**
     * Retrieves a property as a double.
     * 
     * @param key
     *            String
     * @return double
     */
    public double getPropertyAsDouble(String key) {
        return getPropertyAsDouble(key, 0.0);
    }

    /**
     * Retrieves a property as a double.
     * 
     * @param key
     *            String
     * @param defaultValue
     *            double
     * @return double
     */
    public double getPropertyAsDouble(String key, double defaultValue) {
        String value = getProperty(key);
        if (value == null)
            return defaultValue;
        value = value.trim();

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a property as a float.
     * 
     * @param key
     *            String
     * @return float
     */
    public float getPropertyAsFloat(String key) {
        return getPropertyAsFloat(key, 0.0f);
    }

    /**
     * Retrieves a property as a float.
     * 
     * @param key
     *            String
     * @param defaultValue
     *            float
     * @return float
     */
    public float getPropertyAsFloat(String key, float defaultValue) {
        String value = getProperty(key);
        if (value == null)
            return defaultValue;
        value = value.trim();

        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a property as a int.
     * 
     * @param key
     *            String
     * @return int
     */
    public int getPropertyAsInt(String key) {
        return getPropertyAsInt(key, 0);
    }

    /**
     * Retrieves a property as a int.
     * 
     * @param key
     *            String
     * @param defaultValue
     *            int
     * @return int
     */
    public int getPropertyAsInt(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null)
            return defaultValue;
        value = value.trim();

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a property as a long.
     * 
     * @param key
     *            String
     * @return long
     */
    public long getPropertyAsLong(String key) {
        return getPropertyAsLong(key, 0);
    }

    /**
     * Retrieves a property as a long.
     * 
     * @param key
     *            String
     * @param defaultValue
     *            long
     * @return long
     */
    public long getPropertyAsLong(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null)
            return defaultValue;
        value = value.trim();

        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Replaces all properties with a new set.
     * 
     * @param properties
     *            Properties.
     * @since 0.3
     */
    public void setProperties(Properties properties) {
        this.clear();
        putAll(properties);
    }

    private void populateInitialValues(Properties initialValues) {
        putAll(initialValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void load(InputStream is) throws IOException {
        try {
            rendering++;
            super.load(is);
        } finally {
            rendering--;
        }
        renderProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void loadFromXML(InputStream is) throws IOException {
        try {
            rendering++;
            super.loadFromXML(is);
        } finally {
            rendering--;
        }
        renderProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public Object put(Object key, Object value) {
        if (key instanceof String && value instanceof String) {
            Object result = super.put(key, value);
            if (rendering == 0) {
                renderProperty((String) key, (String) value);
            }
            return result;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void putAll(Map<? extends Object, ? extends Object> t) {
        if (t == null) {
            return;
        }
        try {
            rendering++;
            super.putAll(t);
        } finally {
            rendering--;
        }
        renderProperties();
    }

    /**
     * Renders all properties.
     */
    synchronized protected void renderProperties() {
        try {
            rendering++;
            for (Iterator<Object> i = this.keySet().iterator(); i.hasNext();) {
                Object key = i.next();
                if (key instanceof String) {
                    renderProperty((String) key);
                }
            }
        } finally {
            rendering--;
        }
    }

    /**
     * Renders a specified property.
     */
    synchronized protected void renderProperty(String key) {
        String value = getProperty(key);
        renderProperty(key, value);
    }

    private Map<String, Object> isRendering = new HashMap<String, Object>();

    synchronized protected void renderProperty(String key, String value) {
        if (key == null || value == null)
            return;

        if (isRendering.containsKey(key))
            throw new IllegalStateException("Circular referencing detected!");

        try {
            rendering++;
            Matcher m = PATTERN.matcher(value);
            StringBuffer sb = new StringBuffer();
            boolean render = false;
            while (m.find()) {
                if (!render) {
                    isRendering.put(key, value);
                    render = true;
                }
                String replacement = replace(m.group(1));
                m.appendReplacement(sb, RegExpUtils.regexpReplacementEscape(replacement));
            }
            m.appendTail(sb);
            super.put(key, sb.toString());
        } finally {
            rendering--;
            isRendering.remove(key);
        }
    }

    private String replace(String pattern) {
        if (this.containsKey(pattern)) {
            this.renderProperty(pattern);
            return this.getProperty(pattern);
        }

        String value = System.getProperty(pattern);
        if (value != null)
            return value;

        return "";
    }
}
