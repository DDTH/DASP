package ddth.dasp.framework.bo;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJdbcBo implements IJdbcBo {

    private Logger LOGGER = LoggerFactory.getLogger(BaseJdbcBo.class);

    protected Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0.0;
        }
        if (value instanceof Character) {
            char c = ((Character) value).charValue();
            return c == 'Y' || c == 'y' || c == 'T' || c == 't';
        }
        if (value instanceof String) {
            return "YES".equalsIgnoreCase(value.toString())
                    || "Y".equalsIgnoreCase(value.toString())
                    || "TRUE".equalsIgnoreCase(value.toString())
                    || "T".equalsIgnoreCase(value.toString());
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SQLException
     */
    @Override
    public void populate(ResultSet rs) throws SQLException {
        Map<String, Object[]> fieldMap = getFieldMap();
        Class<?> myClass = getClass();
        for (Entry<String, Object[]> entry : fieldMap.entrySet()) {
            Object[] boInfo = entry.getValue();
            String dbCol = entry.getKey();
            String boAttr = boInfo[0].toString();
            Class<?> boAttrType = (Class<?>) boInfo[1];
            Object boValue = rs.getObject(dbCol);

            if (boAttrType == boolean.class || boAttrType == Boolean.class) {
                boValue = toBoolean(boValue);
            }

            String methodName = "set" + WordUtils.capitalize(boAttr);
            if (LOGGER.isDebugEnabled()) {
                String msg = "Calling method " + methodName + "(" + boValue
                        + ") to populate db column [" + dbCol + "].";
                LOGGER.debug(msg);
            }
            try {
                Method method = myClass.getDeclaredMethod(methodName, boAttrType);
                if (method != null) {
                    method.setAccessible(true);
                    method.invoke(this, boValue);
                } else {
                    LOGGER.warn("No setter method found [" + methodName + "] for class [" + myClass
                            + "]!");
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
