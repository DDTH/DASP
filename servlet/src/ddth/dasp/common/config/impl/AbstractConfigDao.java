package ddth.dasp.common.config.impl;

import ddth.dasp.common.config.IConfigDao;

public abstract class AbstractConfigDao implements IConfigDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getConfigAsBoolean(String module, String key) {
        Object value = getConfig(module, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String valueStr = value.toString();
        if ("Y".equalsIgnoreCase(valueStr) || "T".equalsIgnoreCase(valueStr)
                || "1".equalsIgnoreCase(valueStr)) {
            return Boolean.TRUE;
        }
        if ("N".equalsIgnoreCase(valueStr) || "F".equalsIgnoreCase(valueStr)
                || "0".equalsIgnoreCase(valueStr)) {
            return Boolean.FALSE;
        }
        try {
            return Boolean.parseBoolean(valueStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getConfigAsDouble(String module, String key) {
        Object value = getConfig(module, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        String valueStr = value.toString();
        try {
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getConfigAsFloat(String module, String key) {
        Object value = getConfig(module, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        String valueStr = value.toString();
        try {
            return Float.parseFloat(valueStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getConfigAsInteger(String module, String key) {
        Object value = getConfig(module, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String valueStr = value.toString();
        try {
            return Integer.parseInt(valueStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getConfigAsLong(String module, String key) {
        Object value = getConfig(module, key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String valueStr = value.toString();
        try {
            return Long.parseLong(valueStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigAsString(String module, String key) {
        Object value = getConfig(module, key);
        return value != null ? value.toString() : null;
    }
}
