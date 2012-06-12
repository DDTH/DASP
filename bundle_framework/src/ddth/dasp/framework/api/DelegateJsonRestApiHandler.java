package ddth.dasp.framework.api;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.servlet.rp.IRequestParser;
import ddth.dasp.utils.DaspConstants;
import ddth.dasp.utils.JsonUtils;

/**
 * This {@link IApiHandler} uses JSON as message format.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DelegateJsonRestApiHandler extends DelegateRestApiHandler
		implements Controller {

	private Logger LOGGER = LoggerFactory
			.getLogger(DelegateJsonRestApiHandler.class);

	public DelegateJsonRestApiHandler() {
	}

	public DelegateJsonRestApiHandler(IApiHandler apiHandler) {
		super(apiHandler);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This methods first parses the request's content (POST method expected) as
	 * a JSON string; then, add parameters from URL (if any).
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return Object
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected Object parseInput(HttpServletRequest request) throws Exception {
		Object tempRp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
		if (!(tempRp instanceof IRequestParser)) {
			LOGGER.warn("No instance of [" + IRequestParser.class + "] found!");
			return null;
		}
		IRequestParser rp = (IRequestParser) tempRp;
		String rawInput = rp.getRequestContent();
		try {
			// first: parses parameters from request's content as JSON.
			Object result = JsonUtils.fromJson(rawInput);

			// second: add parameters from URL if applicable.
			if (result instanceof Map<?, ?>) {
				Map<String, String> tempMap = (Map<String, String>) result;
				for (Entry<String, String> entry : rp.getUrlParameters()
						.entrySet()) {
					String key = entry.getKey();
					if (!tempMap.containsKey(key)) {
						tempMap.put(key, entry.getValue());
					}
				}
			}
			return result;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This method sends result as a JSON string to the http response.
	 */
	@Override
	protected void returnResult(HttpServletResponse response, Object result)
			throws Exception {
		String json = null;
		try {
			json = JsonUtils.toJson(result);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.flushBuffer();
	}
}
