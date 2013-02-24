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
    public String getAction(IRequest request) {
        return getPathParam(request, PATH_PARAM_INDEX_ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModule(IRequest request) {
        return getPathParam(request, PATH_PARAM_INDEX_MODULE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPathParam(IRequest request, int index) {
        String[] pathParams = request.getPathParams();
        String result = 0 <= index && index < pathParams.length ? pathParams[index] : null;
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
    public String getUrlParam(IRequest request, String name) {
        return request.getUrlParams().get(name);
    }
}
