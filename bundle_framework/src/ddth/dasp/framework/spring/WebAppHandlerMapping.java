package ddth.dasp.framework.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

/**
 * This {@link HandlerMapping} is similar to {@link SimpleHandlerMapping} but
 * also handle static resources (e.g. css, javascript, etc).
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class WebAppHandlerMapping extends SimpleHandlerMapping {

	private Logger LOGGER = LoggerFactory.getLogger(WebAppHandlerMapping.class);
	private Map<String, ?> staticResourceHandlerMapping;

	protected Map<String, ?> getStaticResourceHandlerMapping() {
		return staticResourceHandlerMapping;
	}

	public void setStaticResourceHandlerMapping(Map<String, ?> handlerMapping) {
		this.staticResourceHandlerMapping = handlerMapping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		if (staticResourceHandlerMapping == null
				|| staticResourceHandlerMapping.size() == 0) {
			return super.getHandlerInternal(request);
		}
		String uri = request.getRequestURI();
		int index = uri.lastIndexOf('.');
		if (index < 0) {
			return super.getHandlerInternal(request);
		}
		String ext = uri.substring(index);
		Object controller = staticResourceHandlerMapping.get(ext);
		if (controller != null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Found handler for [" + ext + "].";
				LOGGER.debug(msg);
			}
			return controller;
		}
		return super.getHandlerInternal(request);
	}
}
