package ddth.dasp.servlet.thrift.api;

import ddth.dasp.common.RequestLocal;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.common.utils.JsonUtils;

public class JsonApiHandler implements DaspJsonApi.Iface {
	@Override
	public String callApi(String moduleName, String functionName,
			String jsonEncodedInput, String authKey)
			throws org.apache.thrift.TException {
		// init the request local and bound it to the current thread if needed.
		RequestLocal oldRequestLocal = RequestLocal.get();
		RequestLocal.set(new RequestLocal());
		try {
			Object apiParams = null;
			try {
				apiParams = JsonUtils.fromJson(jsonEncodedInput);
			} catch (Exception e) {
				//
			}
			Object result = ApiUtils.executeApi(moduleName, functionName,
					apiParams, authKey, IApiHandler.REMOTE_THRIFT);
			return JsonUtils.toJson(result);
		} finally {
			RequestLocal.set(oldRequestLocal);
		}
	}
}
