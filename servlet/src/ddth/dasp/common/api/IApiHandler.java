package ddth.dasp.common.api;

public interface IApiHandler {

    public final static String PROP_MODULE = "Module";
    public final static String PROP_API = "Api";
    public final static String RESULT_FIELD_STATUS = "status";
    public final static String RESULT_FIELD_MESSAGE = "message";

    /**
     * Handles an API call.
     * 
     * @param params
     * @param authKey
     * @return
     * @throws ApiException
     */
    public Object callApi(Object params, String authKey) throws ApiException;
}
