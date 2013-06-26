package ddth.dasp.framework.bo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private Map<String, Object[]> dataMappings;

    /**
     * Sets BO data mappings.
     * 
     * @param dataMappings
     * @see IBo#getDataMappings()
     */
    public void setDataMappings(Map<String, Object[]> dataMappings) {
        this.dataMappings = dataMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object[]> getDataMappings() {
        return dataMappings;
    }

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

    protected Character toCharacter(Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString();
        return str.length() > 0 ? str.charAt(0) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Map<String, ?> data) {
        Map<String, Object[]> dataMappings = getDataMappings();
        if (dataMappings == null) {
            populateWithAnnotations(data);
        } else {
            populateWithDataMappings(data, dataMappings);
        }
    }

    protected void populateWithAnnotations(Map<String, ?> data) {
        Class<?> clazz = this.getClass();
        Class<? extends Annotation> annoClazz = FieldMapping.class;
        while (clazz != Object.class) {
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(clazz
                    .getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annoClazz)) {
                    FieldMapping annotation = (FieldMapping) method.getAnnotation(annoClazz);
                    String field = annotation.field();
                    Object value = data.get(field);
                    if (value == null) {
                        String msg = "Found setter method [" + method.getName() + "] for field ["
                                + field + "], but no value fould!";
                        LOGGER.warn(msg);
                    } else {
                        if (!annotation.type().isAssignableFrom(value.getClass())) {
                            String msg = "Value of type [" + value.getClass() + "] mismatches ["
                                    + annotation.type() + "]!";
                            throw new RuntimeException(msg);
                        } else {
                            try {
                                method.invoke(this, value);
                            } catch (Exception e) {
                                if (e instanceof RuntimeException) {
                                    throw (RuntimeException) e;
                                } else {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    protected void populateWithDataMappings(Map<String, ?> data, Map<String, Object[]> dataMappings) {
        Class<?> myClass = getClass();
        for (Entry<String, Object[]> entry : dataMappings.entrySet()) {
            String externalField = entry.getKey();
            Object[] boInfo = entry.getValue();
            String boAttr = boInfo[0].toString();
            Class<?> boAttrType = (Class<?>) boInfo[1];
            Object boValue = data.get(externalField);

            if ((boAttrType == boolean.class || boAttrType == Boolean.class)
                    && !(boValue instanceof Boolean)) {
                boValue = toBoolean(boValue);
            } else if ((boAttrType == char.class || boAttrType == Character.class)
                    && !(boValue instanceof Character)) {
                boValue = toCharacter(boValue);
            }

            String methodName = "set" + WordUtils.capitalize(boAttr);
            if (LOGGER.isDebugEnabled()) {
                String msg = "Calling method " + methodName + "(" + boValue
                        + ") to populate db column [" + externalField + "].";
                LOGGER.debug(msg);
            }
            try {
                // Method method = myClass.getDeclaredMethod(methodName,
                // boAttrType);
                Method method = myClass.getMethod(methodName, boAttrType);
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
