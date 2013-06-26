package ddth.dasp.framework.api;

import java.util.Map;

public class ApiParameter<T> {
    private String name;
    private Class<T> clazz;

    public ApiParameter(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Extracts parameter value from input map.
     * 
     * @param inputs
     * @return
     */
    public T extractValue(Map<String, Object> inputs) {
        return extractValue(inputs.get(name));
    }

    /**
     * Extracts parameter value from input.
     * 
     * @param input
     * @return
     */
    @SuppressWarnings("unchecked")
    public T extractValue(Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof Map<?, ?>) {
            return extractValue((Map<String, Object>) input);
        }
        if (clazz.isAssignableFrom(input.getClass())) {
            return (T) input;
        }

        if (clazz == Integer.class) {
            Object result = Integer.parseInt(input.toString());
            return (T) result;
        }
        if (clazz == Long.class) {
            Object result = Long.parseLong(input.toString());
            return (T) result;
        }
        if (clazz == Float.class) {
            Object result = Float.parseFloat(input.toString());
            return (T) result;
        }
        if (clazz == Double.class) {
            Object result = Double.parseDouble(input.toString());
            return (T) result;
        }
        return (T) input;
    }
}
