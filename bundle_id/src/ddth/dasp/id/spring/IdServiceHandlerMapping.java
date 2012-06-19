package ddth.dasp.id.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.spring.SimpleHandlerMapping;
import ddth.dasp.framework.api.DelegateJsonRestApiHandler;
import ddth.dasp.id.api.AbstractIdApiHandler;
import ddth.dasp.id.api.Id128Apihandler;
import ddth.dasp.id.api.Id128HexApihandler;
import ddth.dasp.id.api.Id64Apihandler;
import ddth.dasp.id.api.Id64HexApihandler;

public class IdServiceHandlerMapping extends SimpleHandlerMapping {

	private Map<String, Controller> handlerMapping = new HashMap<String, Controller>();

	public IdServiceHandlerMapping(IdGenerator idGen) {
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
	protected Map<String, Controller> getHandlerMapping() {
		return handlerMapping;
	}
}
