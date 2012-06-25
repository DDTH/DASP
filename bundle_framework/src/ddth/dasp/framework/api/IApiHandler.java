package ddth.dasp.framework.api;

public interface IApiHandler {
    /**
     * Handles an API call.
     * 
     * @param params
     *            object
     * @param authKey
     *            String
     * @return Object
     * @throws ApiException
     */
    public Object handleApiCall(Object params, String authKey) throws ApiException;
}
