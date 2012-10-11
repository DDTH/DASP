package ddth.dasp.servlet.thrift;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.common.utils.JsonUtils;

public class DaspJsonApiHandler implements DaspJsonApi.Iface {
	@Override
	public String callApi(String moduleName, String functionName,
			String jsonEncodedInput, String authKey)
			throws org.apache.thrift.TException {
		Object apiParams = JsonUtils.fromJson(jsonEncodedInput);
		Object result = ApiUtils.executeApi(moduleName, functionName,
				apiParams, authKey, IApiHandler.REMOTE_THRIFT);
		return JsonUtils.toJson(result);
	}
}
