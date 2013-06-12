package ddth.dasp.status.osgi;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;

import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.framework.osgi.BaseSpringBundleActivator;
import ddth.dasp.status.DaspBundleConstants;

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
    protected HandlerMapping getSpringMvcHandlerMapping() {
        return getSpringBean(HandlerMapping.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewResolver getSpringMvcViewResolver() {
        return getSpringBean(ViewResolver.class);
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
