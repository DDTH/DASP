package ddth.dasp.servlet.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class DaspHandlerMapping extends AbstractHandlerMapping {

	private Logger LOGGER = LoggerFactory.getLogger(DaspHandlerMapping.class);

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		LOGGER.debug(request.toString());
		return null;
	}

}
