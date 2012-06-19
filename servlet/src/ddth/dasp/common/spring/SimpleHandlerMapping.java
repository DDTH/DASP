package ddth.dasp.common.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This {@link HandlerMapping} obtains a Map&lt;String, Controller&gt; and
 * simply looks up the handler from the map.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class SimpleHandlerMapping extends AbstractHandlerMapping {

	private Logger LOGGER = LoggerFactory.getLogger(SimpleHandlerMapping.class);

	protected abstract Map<String, Controller> getHandlerMapping();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getHandlerInternal(HttpServletRequest request,
			String actionName) throws Exception {
		Map<String, Controller> handlerMapping = getHandlerMapping();
		if (handlerMapping == null) {
			return null;
		}
		Controller controller = handlerMapping.get(actionName);
		if (controller != null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Found handler for action [" + actionName + "].";
				LOGGER.debug(msg);
			}
			return controller;
		}
		controller = handlerMapping.get("*");
		if (controller != null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Found universal handler for action ["
						+ actionName + "].";
				LOGGER.debug(msg);
			}
			return controller;
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = "Found no handler for action [" + actionName + "].";
			LOGGER.debug(msg);
		}
		return null;
	}
}
