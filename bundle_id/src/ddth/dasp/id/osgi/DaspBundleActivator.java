package ddth.dasp.id.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.framework.api.DelegateJsonRestApiHandler;
import ddth.dasp.framework.osgi.BaseBundleActivator;
import ddth.dasp.framework.osgi.ServiceInfo;
import ddth.dasp.framework.spring.SimpleHandlerMapping;
import ddth.dasp.id.DaspBundleConstants;
import ddth.dasp.id.api.AbstractIdApiHandler;
import ddth.dasp.id.api.Id128Apihandler;
import ddth.dasp.id.api.Id128HexApihandler;
import ddth.dasp.id.api.Id64Apihandler;
import ddth.dasp.id.api.Id64HexApihandler;

public class DaspBundleActivator extends BaseBundleActivator {

    private IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());
    private AbstractIdApiHandler id64 = new Id64Apihandler(idGen);
    private AbstractIdApiHandler id64Hex = new Id64HexApihandler(idGen);
    private AbstractIdApiHandler id128 = new Id128Apihandler(idGen);
    private AbstractIdApiHandler id128Hex = new Id128HexApihandler(idGen);

    private HandlerMapping idServiceHandlerMapping;

    private List<ServiceInfo> serviceInfoList;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ServiceInfo> getServiceInfoList() {
        if (serviceInfoList == null) {
            serviceInfoList = new ArrayList<ServiceInfo>();
            Properties props = new Properties();

            props.put(IApiHandler.PROP_API, "id64");
            ServiceInfo serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id64, props);
            serviceInfoList.add(serviceInfo);

            props.put(IApiHandler.PROP_API, "id64hex");
            serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id64Hex, props);
            serviceInfoList.add(serviceInfo);

            props.put(IApiHandler.PROP_API, "id128");
            serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id128, props);
            serviceInfoList.add(serviceInfo);

            props.put(IApiHandler.PROP_API, "id128hex");
            serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id128Hex, props);
            serviceInfoList.add(serviceInfo);
        }
        return serviceInfoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        IdGenerator.disposeInstance(idGen);
        super.stop(bundleContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModuleName() {
        return DaspBundleConstants.MODULE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HandlerMapping getSpringMvcHandlerMapping() {
        if (idServiceHandlerMapping == null) {
            idServiceHandlerMapping = new SimpleHandlerMapping() {
                private Map<String, Controller> handlerMapping;

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Map<String, Controller> getHandlerMapping() {
                    if (handlerMapping == null) {
                        handlerMapping = new HashMap<String, Controller>();
                        handlerMapping.put("id64", new DelegateJsonRestApiHandler(id64));
                        handlerMapping.put("id64hex", new DelegateJsonRestApiHandler(id64Hex));
                        handlerMapping.put("id128", new DelegateJsonRestApiHandler(id128));
                        handlerMapping.put("id128hex", new DelegateJsonRestApiHandler(id128Hex));
                    }
                    return handlerMapping;
                }
            };
        }
        return idServiceHandlerMapping;
    }
}
