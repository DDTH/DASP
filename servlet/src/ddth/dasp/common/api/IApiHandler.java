package ddth.dasp.common.api;

public interface IApiHandler {

    public final static String PROP_MODULE = "Module";
    public final static String PROP_API = "Api";

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
