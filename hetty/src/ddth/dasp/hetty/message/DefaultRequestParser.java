package ddth.dasp.hetty.message;

import org.apache.commons.lang3.StringUtils;

/**
 * This request parser assumes the first (index 0) path parameter is the request
 * module and the second (index 1) path parameter is the request action.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 */
public class DefaultRequestParser implements IRequestParser {

    public final static int PATH_PARAM_INDEX_MODULE = 0;
    public final static int PATH_PARAM_INDEX_ACTION = 1;

    private String urlSuffix;

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    protected String getUrlSuffix() {
        return urlSuffix;
    }

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
        String result = RequestUtils.getPathParam(requestProtobuf, index);
        if (!StringUtils.isBlank(result) && !StringUtils.isBlank(urlSuffix)
                && result.endsWith(urlSuffix)) {
            return result.substring(0, result.length() - urlSuffix.length());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlParam(HettyProtoBuf.Request requestProtobuf, String name) {
        return RequestUtils.getUrlParam(requestProtobuf, name);
    }
}
