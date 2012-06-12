package ddth.dasp.id.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.spring.AbstractHandlerMapping;
import ddth.dasp.framework.api.DelegateJsonRestApiHandler;
import ddth.dasp.id.IdGenerator;

public class IdServiceHandlerMapping extends AbstractHandlerMapping {

	private Map<String, Controller> handlerMapping;
	private IdGenerator idGen;

	public IdServiceHandlerMapping() {
		handlerMapping = new HashMap<String, Controller>();

		AbstractIdApiHandler id64 = new Id64Apihandler(idGen);
		handlerMapping.put("id64", new DelegateJsonRestApiHandler(id64));

		AbstractIdApiHandler id64Hex = new Id64HexApihandler(idGen);
		handlerMapping.put("id64hex", new DelegateJsonRestApiHandler(id64Hex));

		AbstractIdApiHandler id128 = new Id128Apihandler(idGen);
		handlerMapping.put("id128", new DelegateJsonRestApiHandler(id128));

		AbstractIdApiHandler id128Hex = new Id128HexApihandler(idGen);
		handlerMapping
				.put("id128hex", new DelegateJsonRestApiHandler(id128Hex));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getHandlerInternal(HttpServletRequest request,
			String actionName) throws Exception {
		return handlerMapping.get(actionName);
	}
}
