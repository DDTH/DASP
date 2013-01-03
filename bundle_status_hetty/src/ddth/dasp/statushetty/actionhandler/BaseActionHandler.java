package ddth.dasp.statushetty.actionhandler;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.hetty.message.protobuf.HettyProtoBuf;
import ddth.dasp.hetty.mvc.SimpleActionHandler;
import ddth.dasp.hetty.mvc.view.IView;
import ddth.dasp.hetty.mvc.view.IViewResolver;
import ddth.dasp.statushetty.DaspBundleConstants;

public class BaseActionHandler extends SimpleActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected IView resolveVew(HettyProtoBuf.Request request, String viewName) {
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("skin", "default");
        IViewResolver viewResolver = getViewResolver();
        return viewResolver.resolveView(viewName, replacements);
    }

    private final static String SKIN_ROOT = "/" + DaspBundleConstants.MODULE_NAME
            + "/static/default";
    private final static String URL_SERVER_STATUS = "/" + DaspBundleConstants.MODULE_NAME
            + "/server";
    private final static String URL_OSGI_STATUS = "/" + DaspBundleConstants.MODULE_NAME + "/osgi";
    private final static String URL_CACHE_STATUS = "/" + DaspBundleConstants.MODULE_NAME + "/cache";
    private final static String URL_JDBC_STATUS = "/" + DaspBundleConstants.MODULE_NAME + "/jdbc";

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel() {
        Map<String, Object> model = super.buildViewModel();
        model.put("SKIN_ROOT", SKIN_ROOT);
        model.put("urlServerStatus", URL_SERVER_STATUS);
        model.put("urlOsgiStatus", URL_OSGI_STATUS);
        model.put("urlCacheStatus", URL_CACHE_STATUS);
        model.put("urlJdbcStatus", URL_JDBC_STATUS);
        return model;
    }
}
