package ddth.dasp.framework.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ddth.dasp.framework.dbc.DbcpJdbcFactory;
import ddth.dasp.framework.dbc.IJdbcFactory;

public class BundleActivator extends BaseBundleActivator {
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ServiceInfo> getServiceInfoList() {
        List<ServiceInfo> serviceInfoList = super.getServiceInfoList();
        if (serviceInfoList == null) {
            serviceInfoList = new ArrayList<ServiceInfo>();
        }

        // register a default IJdbcFactory
        Properties props = new Properties();
        IJdbcFactory jdbcFactory = new DbcpJdbcFactory();
        jdbcFactory.init();
        ServiceInfo serviceInfo = new ServiceInfo(IJdbcFactory.class.getName(), jdbcFactory, props);
        serviceInfoList.add(serviceInfo);

        return serviceInfoList;
    }
}
