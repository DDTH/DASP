package ddth.dasp.common.utils;

import java.util.Map;

public class UrlUtils {
	/**
	 * Creates a URL with default settings.
	 * 
	 * @param actions
	 * @param params
	 * @return
	 */
	public static String createUrl(String[] actions, Map<String, ?> params) {
		return null;
		// ServletContext servletContext =
		// ServletActionContext.getServletContext();
		// StringBuilder url = new
		// StringBuilder(servletContext.getContextPath());
		// for (String action : actions) {
		// url.append("/").append(action);
		// }
		// url.append(DaspConstants.URL_SUFFIX);
		//
		// if (params != null && params.size() > 0) {
		// url.append("?");
		// for (Entry<String, ?> entry : params.entrySet()) {
		// url.append(entry.getKey());
		// url.append("=");
		// url.append(entry.getValue());
		// url.append("&");
		// }
		// url.setLength(url.length() - 1);
		// }
		//
		// HttpServletResponse servletResponse =
		// ServletActionContext.getResponse();
		// return servletResponse.encodeURL(url.toString());
	}
}
