package ddth.dasp.servlet.thrift;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.api.IApiGroupHandler;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.JsonUtils;

public class DaspJsonApiHandler implements DaspJsonApi.Iface {
    @Override
    public String callApi(String moduleName, String functionName, String jsonEncodedInput,
            String authKey) throws org.apache.thrift.TException {
        Object result;
        try {
            IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
            Map<String, String> filter = new HashMap<String, String>();
            filter.put("Module", moduleName);
            filter.put("Api", functionName);
            IApiHandler apiHandler = osgiBootstrap.getService(IApiHandler.class, filter);
            if (apiHandler != null) {
                Object params = JsonUtils.fromJson(jsonEncodedInput);
                result = apiHandler.callApi(params, authKey);
            } else {
                filter.remove("Api");
                IApiGroupHandler apiGroupHandler = osgiBootstrap.getService(IApiGroupHandler.class,
                        filter);
                if (apiGroupHandler != null) {
                    Object params = JsonUtils.fromJson(jsonEncodedInput);
                    result = apiGroupHandler.handleApiCall(functionName, params, authKey);
                } else {
                    Map<Object, Object> res = new HashMap<Object, Object>();
                    res.put("status", 404);
                    res.put("message", "No handler for [" + moduleName + "/" + functionName + "]!");
                    result = res;
                }
            }
        } catch (Exception e) {
            Map<Object, Object> res = new HashMap<Object, Object>();
            res.put("status", 500);
            res.put("message", e.getMessage());
            result = res;
        }
        return JsonUtils.toJson(result);
    }
}
