package ddth.dasp.statushetty.actionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.OsgiUtils;

public class OsgiStatusActionHandler extends BaseActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel() {
        Map<String, Object> model = super.buildViewModel();
        model.put("OSGI", buildModelOsgi());
        return model;
    }

    private Object buildModelOsgi() {
        List<Object> model = new ArrayList<Object>();

        IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
        BundleContext bc = osgiBootstrap.getBundleContext();
        Bundle[] bundles = bc.getBundles();

        for (Bundle bundle : bundles) {
            Map<String, Object> bundleModel = new HashMap<String, Object>();
            model.add(bundleModel);
            bundleModel.put("id", bundle.getBundleId());
            bundleModel.put("name", bundle.getSymbolicName() != null ? bundle.getSymbolicName()
                    : "null");
            bundleModel.put("version", bundle.getVersion());
            bundleModel.put("state", OsgiUtils.getBundleStateAsString(bundle));
        }

        return model;
    }
}
