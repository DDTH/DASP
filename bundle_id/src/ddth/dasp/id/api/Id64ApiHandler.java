package ddth.dasp.id.api;

import java.util.Properties;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.framework.osgi.IServiceAutoRegister;
import ddth.dasp.osgi.springaop.profiling.MethodProfile;

public class Id64ApiHandler extends AbstractIdApiHandler implements
		IServiceAutoRegister {

	public Id64ApiHandler() {
	}

	public Id64ApiHandler(IdGenerator idGen) {
		super(idGen);
	}

	@MethodProfile
	@Override
	protected Object internalCallApi(Object params, String authKey,
			String remoteAddr) {
		return ApiUtils.createApiResult(IApiHandler.RESULT_CODE_OK,
				getIdGenerator().generateId64());
	}

	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		props.put(IApiHandler.PROP_API, "id64");
		return props;
	}
}
