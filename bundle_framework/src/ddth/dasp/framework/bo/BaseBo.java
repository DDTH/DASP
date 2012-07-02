package ddth.dasp.framework.bo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class as starting point for Business Object implementation.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseBo implements IBo {

    private Logger LOGGER = LoggerFactory.getLogger(BaseBo.class);

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
     */
    @Override
    public void populate(Map<String, ?> data) {
        Map<String, Object[]> dataMappings = getDataMappings();
        Class<?> myClass = getClass();
        for (Entry<String, Object[]> entry : dataMappings.entrySet()) {
            String externalField = entry.getKey();
            Object[] boInfo = entry.getValue();
            String boAttr = boInfo[0].toString();
            Class<?> boAttrType = (Class<?>) boInfo[1];
            Object boValue = data.get(externalField);

            if (boAttrType == boolean.class || boAttrType == Boolean.class) {
                boValue = toBoolean(boValue);
            }

            String methodName = "set" + WordUtils.capitalize(boAttr);
            if (LOGGER.isDebugEnabled()) {
                String msg = "Calling method " + methodName + "(" + boValue
                        + ") to populate db column [" + externalField + "].";
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
