package ddth.dasp.framework.api;

import ddth.dasp.common.api.ApiException;
import ddth.dasp.common.api.IApiGroupHandler;

public abstract class AbstractApiGroupHandler implements IApiGroupHandler {

	/**
	 * Validates the authKey.
	 * 
	 * This method simply returns <code>true</code>. Sub-class overrides this
	 * method to perform its own business.
	 * 
	 * @param apiName
	 * @param authKey
	 * @param remoteAddr
	 * @return boolean
	 */
	protected boolean validateAuthKey(String apiName, String authKey,
			String remoteAddr) {
		return true;
	}

	/**
	 * Validates input parameters.
	 * 
	 * This method simply returns <code>true</code>. Sub-class overrides this
	 * method to perform its own business.
	 * 
	 * @param apiName
	 * @param params
	 * @param remoteAddr
	 * @return boolean
	 */
	protected boolean validateParams(String apiName, Object params,
			String remoteAddr) {
		return true;
	}

	/**
	 * Called by {@link #callApi(Object, String)}. Sub-class overrides this
	 * method to perform is own business.
	 * 
	 * @param apiName
	 * @param params
	 * @param authKey
	 * @param remoteAddr
	 * @return Object
	 * @throws Exception
	 */
	protected abstract Object internalHandleApiCall(String apiName,
			Object params, String authKey, String remoteAddr)
			throws ApiException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object handleApiCall(String apiName, Object params, String authKey,
			String remoteAddr) throws ApiException {
		if (!validateAuthKey(apiName, authKey, remoteAddr)) {
			String msg = "Authkey [" + authKey + "] for api [" + apiName
					+ "] validation failed!";
			throw new ApiException(msg);
		}
		if (!validateParams(apiName, params, remoteAddr)) {
			String msg = "Input params [" + params + "] for api [" + apiName
					+ "] validation failed!";
			throw new ApiException(msg);
		}
		return internalHandleApiCall(apiName, params, authKey, remoteAddr);
	}
}
