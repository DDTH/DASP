package ddth.dasp.framework.api;

import ddth.dasp.common.api.ApiException;
import ddth.dasp.common.api.IApiHandler;

public abstract class AbstractApiHandler implements IApiHandler {

	/**
	 * Validates the authKey.
	 * 
	 * This method simply returns <code>true</code>. Sub-class overrides this
	 * method to perform its own business.
	 * 
	 * @param authKey
	 * @param remoteAddr
	 * @return boolean
	 */
	protected boolean validateAuthKey(String authKey, String remoteAddr) {
		return true;
	}

	/**
	 * Validates input parameters.
	 * 
	 * This method simply returns <code>true</code>. Sub-class overrides this
	 * method to perform its own business.
	 * 
	 * @param params
	 * @param remoteAddr
	 * @return boolean
	 */
	protected boolean validateParams(Object params, String remoteAddr) {
		return true;
	}

	/**
	 * Called by {@link #callApi(Object, String)}. Sub-class overrides this
	 * method to perform is own business.
	 * 
	 * @param params
	 * @param authKey
	 * @param remoteAddr
	 * @return Object
	 * @throws Exception
	 */
	protected abstract Object internalCallApi(Object params, String authKey,
			String remoteAddr) throws ApiException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object callApi(Object params, String authKey, String remoteAddr)
			throws ApiException {
		if (!validateAuthKey(authKey, remoteAddr)) {
			String msg = "Authkey [" + authKey + "] validation failed!";
			throw new ApiException(msg);
		}
		if (!validateParams(params, remoteAddr)) {
			String msg = "Input params [" + params + "] validation failed!";
			throw new ApiException(msg);
		}
		return internalCallApi(params, authKey, remoteAddr);
	}
}
