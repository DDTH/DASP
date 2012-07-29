package ddth.dasp.common.api;

public interface IApiGroupHandler {
    /**
     * Handles an API call.
     * 
     * @param apiName
     * @param params
     * @param authKey
     * @return
     * @throws ApiException
     */
    public Object handleApiCall(String apiName, Object params, String authKey) throws ApiException;
}
