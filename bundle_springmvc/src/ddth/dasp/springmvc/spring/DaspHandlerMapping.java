package ddth.dasp.springmvc.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;

public class DaspHandlerMapping extends AbstractHandlerMapping {

	private Logger LOGGER = LoggerFactory.getLogger(DaspHandlerMapping.class);
	private final static Class<HandlerMapping> SERVICE_CLASS = HandlerMapping.class;

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		Object temp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
		if (!(temp instanceof IRequestParser)) {
			LOGGER.warn("No instance of [" + IRequestParser.class + "] found!");
			return null;
		}

		IRequestParser rp = (IRequestParser) temp;
		String moduleName = rp.getRequestModule();
		if (StringUtils.isBlank(moduleName)) {
			moduleName = "home";
		}

		IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
		if (osgiBootstrap == null) {
			String msg = "Instance of [" + IOsgiBootstrap.class
					+ " not found]!";
			LOGGER.warn(msg);
			return null;
		}
		Map<String, String> filter = new HashMap<String, String>();
		filter.put("Module", moduleName);
		HandlerMapping handlerMapping = osgiBootstrap.getService(SERVICE_CLASS,
				filter);
		if (handlerMapping == null) {
			String msg = "No handler mapping found for module [" + moduleName
					+ "]!";
			LOGGER.warn(msg);
			return null;
		}

		Object result = handlerMapping.getHandler(request);
		if (LOGGER.isDebugEnabled()) {
			String msg = "Found [" + result + "] for module [" + moduleName
					+ "].";
			LOGGER.debug(msg);
		}
		return result;
	}
}
