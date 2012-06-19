package ddth.dasp.status.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.spring.SimpleHandlerMapping;

public class StatusBundleHandlerMapping extends SimpleHandlerMapping {

	private Map<String, Controller> handlerMapping = new HashMap<String, Controller>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Controller> getHandlerMapping() {
		return handlerMapping;
	}

	public void setHandlerMapping(Map<String, Controller> handlerMapping) {
		this.handlerMapping = handlerMapping;
	}
}
