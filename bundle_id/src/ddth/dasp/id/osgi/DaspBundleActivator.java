package ddth.dasp.id.osgi;

import org.osgi.framework.BundleContext;
import org.springframework.web.servlet.HandlerMapping;

import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.osgi.BaseBundleActivator;
import ddth.dasp.id.api.IdServiceHandlerMapping;

public class DaspBundleActivator extends BaseBundleActivator {

	private IdGenerator idGen = IdGenerator.getInstance(IdGenerator
			.getMacAddr());
	private IdServiceHandlerMapping idServiceHandlerMapping = new IdServiceHandlerMapping(idGen);

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
		return "ids";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HandlerMapping getSpringMvcHandlerMapping() {
		return idServiceHandlerMapping;
	}
}
