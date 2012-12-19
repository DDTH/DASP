package ddth.dasp.config.osgi;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.config.DaspBundleConstants;
import ddth.dasp.config.bo.IConfigDao;
import ddth.dasp.framework.osgi.BaseBundleActivator;
import ddth.dasp.framework.osgi.ServiceInfo;

public class DaspBundleActivator extends BaseBundleActivator {

    public final static String OSGI_PROP_DAO_CLASS = "osgi.dasp.config.dao.class";

    private Logger LOGGER = LoggerFactory.getLogger(DaspBundleActivator.class);
    private IConfigDao configDao;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        String daoClass = bundleContext.getProperty(OSGI_PROP_DAO_CLASS);
        Class<IConfigDao> clazz = (Class<IConfigDao>) Class.forName(daoClass);
        configDao = clazz.newInstance();
        configDao.init(bundleContext);

        super.start(bundleContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        try {
            super.stop(bundleContext);
        } finally {
            if (configDao != null) {
                try {
                    configDao.destroy(bundleContext);
                } finally {
                    configDao = null;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ServiceInfo> getServiceInfoList() {
        List<ServiceInfo> serviceInfoList = new ArrayList<ServiceInfo>();
        if (configDao != null) {
            ServiceInfo serviceInfo = new ServiceInfo(IConfigDao.class.getName(), configDao);
            serviceInfoList.add(serviceInfo);
        } else {
            LOGGER.warn("No [" + IConfigDao.class.getName() + "] found!");
        }
        return serviceInfoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModuleName() {
        return DaspBundleConstants.MODULE_NAME;
    }
}
