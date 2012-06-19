package ddth.dasp.status.osgi;

import org.osgi.framework.BundleContext;

import ddth.dasp.common.osgi.BaseBundleActivator;

public class DaspBundleActivator extends BaseBundleActivator {

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModuleName() {
		return "status";
	}
}
