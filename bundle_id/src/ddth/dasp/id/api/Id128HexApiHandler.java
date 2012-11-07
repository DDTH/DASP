package ddth.dasp.id.api;

import java.util.Map;
import java.util.Properties;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.osgi.springaop.profiling.MethodProfile;

public class Id128HexApiHandler extends AbstractIdApiHandler {

	private final static int PADDING = 32;

	public Id128HexApiHandler() {
	}

	public Id128HexApiHandler(IdGenerator idGen) {
		super(idGen);
	}

	@MethodProfile
	@Override
	protected Object internalCallApi(Object params, String authKey,
			String remoteAddr) {
		StringBuffer hex = new StringBuffer(getIdGenerator().generateId128Hex());
		if (params instanceof Map<?, ?>) {
			Map<?, ?> tempMap = (Map<?, ?>) params;
			if (tempMap.get("padding") != null) {
				while (hex.length() < PADDING) {
					hex.insert(0, '0');
				}
			}
		}
		return ApiUtils.createApiResult(IApiHandler.RESULT_CODE_OK,
				hex.toString());
	}

	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		props.put(IApiHandler.PROP_API, "id128hex");
		return props;
	}
}
