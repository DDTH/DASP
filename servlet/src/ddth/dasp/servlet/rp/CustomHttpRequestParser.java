package ddth.dasp.servlet.rp;

import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;

/**
 * This implementation of {@link IRequestParser} allows customization of
 * retrieving request module/action/authkey from the request.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class CustomHttpRequestParser extends HttpRequestParser {

	public final static int PARAM_LOCATION_URI = 0;
	public final static int PARAM_LOCATION_DOMAIN = 1;
	public final static int PARAM_LOCATION_QUERY = 2;

	private String baseDomain, requestModuleName, requestActionName,
			requestAuthKeyName;
	private int requestModuleLocation = PARAM_LOCATION_URI;
	private int requestActionLocation = PARAM_LOCATION_URI;
	private int requestAuthKeyLocation = PARAM_LOCATION_URI;

	public String getBaseDomain() {
		return baseDomain;
	}

	public void setBaseDomain(String baseDomain) {
		this.baseDomain = baseDomain;
	}

	public String getRequestModuleName() {
		return requestModuleName;
	}

	public void setRequestModuleName(String requestModuleName) {
		this.requestModuleName = requestModuleName;
	}

	public String getRequestActionName() {
		return requestActionName;
	}

	public void setRequestActionName(String requestActionName) {
		this.requestActionName = requestActionName;
	}

	public String getRequestAuthKeyName() {
		return requestAuthKeyName;
	}

	public void setRequestAuthKeyName(String requestAuthKeyName) {
		this.requestAuthKeyName = requestAuthKeyName;
	}

	public int getRequestModuleLocation() {
		return requestModuleLocation;
	}

	public void setRequestModuleLocation(int requestModuleLocation) {
		this.requestModuleLocation = requestModuleLocation;
	}

	public int getRequestActionLocation() {
		return requestActionLocation;
	}

	public void setRequestActionLocation(int requestActionLocation) {
		this.requestActionLocation = requestActionLocation;
	}

	public int getRequestAuthKeyLocation() {
		return requestAuthKeyLocation;
	}

	public void setRequestAuthKeyLocation(int requestAuthKeyLocation) {
		this.requestAuthKeyLocation = requestAuthKeyLocation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestModule() {
		return getVirtualParameter(DaspConstants.PARAM_INDEX_MODULE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRequestAction() {
		return getVirtualParameter(DaspConstants.PARAM_INDEX_ACTION);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRequestAuthKey() {
		return getVirtualParameter(DaspConstants.PARAM_INDEX_AUTHKEY);
	}
}
