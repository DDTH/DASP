package ddth.dasp.common.api;

public interface IApiGroupHandler {
	/**
	 * Handles an API call.
	 * 
	 * @param apiName
	 * @param params
	 * @param authKey
	 * @param remoteAddr
	 * @return
	 * @throws ApiException
	 */
	public Object handleApiCall(String apiName, Object params, String authKey,
			String remoteAddr) throws ApiException;
}
