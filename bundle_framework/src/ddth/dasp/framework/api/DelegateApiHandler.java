package ddth.dasp.framework.api;

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
	protected Object internalHandleApiCall(Object params, String authKey)
			throws Exception {
		return apiHandler.handleApiCall(params, authKey);
	}
}
