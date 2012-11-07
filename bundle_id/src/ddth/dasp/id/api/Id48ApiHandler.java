package ddth.dasp.id.api;

import java.util.Properties;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.osgi.springaop.profiling.MethodProfile;

public class Id48ApiHandler extends AbstractIdApiHandler {

	public Id48ApiHandler() {
	}

	public Id48ApiHandler(IdGenerator idGen) {
		super(idGen);
	}

	@MethodProfile
	@Override
	protected Object internalCallApi(Object params, String authKey,
			String remoteAddr) {
		return ApiUtils.createApiResult(IApiHandler.RESULT_CODE_OK,
				getIdGenerator().generateId48());
	}
	
	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		props.put(IApiHandler.PROP_API, "id48");
		return props;
	}
}
