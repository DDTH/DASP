package ddth.dasp.framework.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.api.ApiException;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;

/**
 * This {@link IApiHandler} handles API call via REST method.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class DelegateRestApiHandler extends DelegateApiHandler
		implements Controller {

	private Logger LOGGER = LoggerFactory
			.getLogger(DelegateRestApiHandler.class);

	public DelegateRestApiHandler() {
	}

	public DelegateRestApiHandler(IApiHandler apiHandler) {
		super(apiHandler);
	}

	/**
	 * Extracts the authKey from the request.
	 * 
	 * This method obtains the authKey from the URL:
	 * http://domain/context/module/action/authKey
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 * @throws ApiException
	 * @throws IOException
	 */
	protected String parseAuthKey(HttpServletRequest request)
			throws ApiException, IOException {
		Object temp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
		if (!(temp instanceof IRequestParser)) {
			LOGGER.warn("No instance of [" + IRequestParser.class + "] found!");
			return null;
		}
		IRequestParser rp = (IRequestParser) temp;
		return rp.getVirtualParameter(DaspConstants.PARAM_INDEX_AUTHKEY);
	}

	/**
	 * Extracts API's input parameters from the request.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return Object
	 * @throws ApiException
	 * @throws IOException
	 */
	protected abstract Object parseInput(HttpServletRequest request)
			throws ApiException, IOException;

	/**
	 * Returns API's result.
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param result
	 *            Object
	 * @throws ApiException
	 * @throws IOException
	 */
	protected abstract void returnResult(HttpServletResponse response,
			Object result) throws ApiException, IOException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String authKey = parseAuthKey(request);
		Object params = parseInput(request);
		Object result = callApi(params, authKey, request.getRemoteAddr());
		returnResult(response, result);
		return null;
	}
}
