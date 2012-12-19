package ddth.dasp.id.api;

import java.util.Map;
import java.util.Properties;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.osgi.springaop.profiling.MethodProfile;

public class Id48AsciiApiHandler extends AbstractIdApiHandler {

    private final static int PADDING = 9;

    public Id48AsciiApiHandler() {
    }

    public Id48AsciiApiHandler(IdGenerator idGen) {
        super(idGen);
    }

    @MethodProfile
    @Override
    protected Object internalCallApi(Object params, String authKey, String remoteAddr) {
        StringBuffer ascii = new StringBuffer(getIdGenerator().generateId48Ascii());
        if (params instanceof Map<?, ?>) {
            Map<?, ?> tempMap = (Map<?, ?>) params;
            if (tempMap.get("padding") != null) {
                while (ascii.length() < PADDING) {
                    ascii.insert(0, '0');
                }
            }
        }
        return ApiUtils.createApiResult(IApiHandler.RESULT_CODE_OK, ascii.toString());
    }

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        props.put(IApiHandler.PROP_API, "id48ascii");
        return props;
    }
}
