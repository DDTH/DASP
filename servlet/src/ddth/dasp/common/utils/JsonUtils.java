package ddth.dasp.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSJSON-related utility class.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Encodes a Java object to JSON string.
     * 
     * @param obj
     *            Object
     * @return String
     * @throws IllegalArgumentException
     *             if the object can not be encoded
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Decodes a JSON string to a Java object
     * 
     * @param json
     *            String
     * @return Object
     * @throws IllegalArgumentException
     *             if the JSON string can not be decoded
     */
    public static Object fromJson(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Decodes a JSON to a Java object with specified type.
     * 
     * @param <T>
     * @param json
     *            String
     * @param clazz
     *            Class<T>
     * @return T
     * @throws IllegalArgumentException
     *             if the JSON string can not be decoded as the given type
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == null)
            throw new IllegalArgumentException("The specified type is null");
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
