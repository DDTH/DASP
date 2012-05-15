package ddth.dasp.servlet.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import ddth.dasp.servlet.rp.IRequestParser;
import ddth.dasp.utils.DaspConstants;

public class DaspHandlerMapping extends AbstractHandlerMapping {

	private Logger LOGGER = LoggerFactory.getLogger(DaspHandlerMapping.class);

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		Object temp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
		if (!(temp instanceof IRequestParser)) {
			LOGGER.warn("No instance of IRequestParser found!");
			return null;
		}
		
		LOGGER.debug(request.toString());
		return null;
	}
}
