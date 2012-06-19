package ddth.dasp.status.osgi;

import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;

import ddth.dasp.common.osgi.BaseSpringBundleActivator;
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
}
