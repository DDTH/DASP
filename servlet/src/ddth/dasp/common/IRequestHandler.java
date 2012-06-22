package ddth.dasp.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequestHandler {
	/**
	 * Handles a HTTP request.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
}
