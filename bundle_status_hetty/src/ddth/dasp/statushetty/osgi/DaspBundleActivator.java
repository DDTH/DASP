package ddth.dasp.statushetty.osgi;

import ddth.dasp.framework.osgi.BaseSpringBundleActivator;
import ddth.dasp.statushetty.DaspBundleConstants;

public class DaspBundleActivator extends BaseSpringBundleActivator {

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
    protected String[] getSpringConfigFiles() {
        return new String[] { "META-INF/osgispring/*.xml" };
    }
}
