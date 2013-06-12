package ddth.dasp.statushetty.osgi;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;

import ddth.dasp.common.utils.DaspConstants;
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

    /**
     * {@inheritDoc}
     */
    protected void internalBundleStart(BundleContext bundleContext) throws Exception {
        super.internalBundleStart(bundleContext);
        String bundleExtractDir = System.getProperty(DaspConstants.SYSPROP_BUNDLE_EXTRACT_DIR);
        if (!StringUtils.isBlank(bundleExtractDir)) {
            extractBundleContent("/META-INF/skins", bundleExtractDir);
        }
    }
}
