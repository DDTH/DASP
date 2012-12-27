package ddth.dasp.hetty.message;

import java.util.List;

public class DefaultRequestParser implements IRequestParser {

    public final static int PATH_PARAM_INDEX_MODULE = 0;
    public final static int PATH_PARAM_INDEX_ACTION = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAction(HettyProtoBuf.Request requestProtobuf) {
        return getPathParam(requestProtobuf, PATH_PARAM_INDEX_ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModule(HettyProtoBuf.Request requestProtobuf) {
        return getPathParam(requestProtobuf, PATH_PARAM_INDEX_MODULE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPathParam(HettyProtoBuf.Request requestProtobuf, int index) {
        List<String> pathParams = requestProtobuf.getPathParamsList();
        return (0 <= index && index < pathParams.size()) ? pathParams.get(index) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlParam(HettyProtoBuf.Request requestProtobuf, String name) {
        List<HettyProtoBuf.NameValue> urlParams = requestProtobuf.getUrlParamsList();
        for (HettyProtoBuf.NameValue param : urlParams) {
            if (param.getName().equals(name)) {
                return param.getValue();
            }
        }
        return null;
    }
}
