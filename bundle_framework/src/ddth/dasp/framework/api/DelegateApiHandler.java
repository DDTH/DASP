package ddth.dasp.framework.api;

import ddth.dasp.common.api.ApiException;
import ddth.dasp.common.api.IApiHandler;

public class DelegateApiHandler extends AbstractApiHandler {

    private IApiHandler apiHandler;

    public DelegateApiHandler() {
    }

    public DelegateApiHandler(IApiHandler apiHandler) {
        setApiHandler(apiHandler);
    }

    public IApiHandler getApiHandler() {
        return apiHandler;
    }

    public void setApiHandler(IApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalHandleApiCall(Object params, String authKey) throws ApiException {
        return apiHandler.callApi(params, authKey);
    }
}
