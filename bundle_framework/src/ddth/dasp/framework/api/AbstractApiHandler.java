package ddth.dasp.framework.api;

public abstract class AbstractApiHandler implements IApiHandler {

    /**
     * Validates the authKey.
     * 
     * This method simply returns <code>true</code>. Sub-class overrides this
     * method to perform its own business.
     * 
     * @param authKey
     *            String
     * @return boolean
     */
    protected boolean validateAuthKey(String authKey) {
        return true;
    }

    /**
     * Validates input parameters.
     * 
     * This method simply returns <code>true</code>. Sub-class overrides this
     * method to perform its own business.
     * 
     * @param params
     *            Object
     * @return boolean
     */
    protected boolean validateParams(Object params) {
        return true;
    }

    /**
     * Called by {@link #handleApiCall(Object, String)}. Sub-class overrides
     * this method to perform is own business.
     * 
     * @param params
     *            Object
     * @param authKey
     *            String
     * @return Object
     * @throws Exception
     */
    protected abstract Object internalHandleApiCall(Object params, String authKey)
            throws ApiException;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handleApiCall(Object params, String authKey) throws ApiException {
        if (!validateAuthKey(authKey)) {
            String msg = "Authkey [" + authKey + "] validation failed!";
            throw new ApiException(msg);
        }
        if (!validateParams(params)) {
            String msg = "Input params [" + params + "] validation failed!";
            throw new ApiException(msg);
        }
        return internalHandleApiCall(params, authKey);
    }
}
