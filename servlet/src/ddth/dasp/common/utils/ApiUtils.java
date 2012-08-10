package ddth.dasp.common.utils;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.common.api.IApiHandler;

public class ApiUtils {
    /**
     * Convenient method to create an API's result map.
     * 
     * @param status
     * @param message
     * @return
     */
    public static Map<Object, Object> createApiResult(int status, Object message) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put(IApiHandler.RESULT_FIELD_STATUS, status);
        result.put(IApiHandler.RESULT_FIELD_MESSAGE, message);
        return result;
    }
}
