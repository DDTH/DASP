package ddth.dasp.id.osgi;

import java.util.List;

import org.osgi.framework.BundleContext;

import ddth.dasp.framework.osgi.BaseSpringBundleActivator;
import ddth.dasp.framework.osgi.ServiceInfo;
import ddth.dasp.id.DaspBundleConstants;

public class DaspBundleActivator extends BaseSpringBundleActivator {

	// private IdGenerator idGen = IdGenerator.getInstance(IdGenerator
	// .getMacAddr());
	// private AbstractIdApiHandler id48 = new Id48Apihandler(idGen);
	// private AbstractIdApiHandler id48Hex = new Id48HexApihandler(idGen);
	// private AbstractIdApiHandler id64 = new Id64Apihandler(idGen);
	// private AbstractIdApiHandler id64Hex = new Id64HexApihandler(idGen);
	// private AbstractIdApiHandler id128 = new Id128Apihandler(idGen);
	// private AbstractIdApiHandler id128Hex = new Id128HexApihandler(idGen);

	// private HandlerMapping idServiceHandlerMapping;

	// private List<ServiceInfo> serviceInfoList;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ServiceInfo> getServiceInfoList() {
		return super.getServiceInfoList();
		// if (serviceInfoList == null) {
		// serviceInfoList = new ArrayList<ServiceInfo>();
		// Properties props = new Properties();
		//
		// props.put(IApiHandler.PROP_API, "id48");
		// ServiceInfo serviceInfo = new ServiceInfo(
		// IApiHandler.class.getName(), id48, props);
		// serviceInfoList.add(serviceInfo);
		//
		// props.put(IApiHandler.PROP_API, "id48hex");
		// serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id48Hex,
		// props);
		// serviceInfoList.add(serviceInfo);
		//
		// props.put(IApiHandler.PROP_API, "id64");
		// serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id64,
		// props);
		// serviceInfoList.add(serviceInfo);
		//
		// props.put(IApiHandler.PROP_API, "id64hex");
		// serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id64Hex,
		// props);
		// serviceInfoList.add(serviceInfo);
		//
		// props.put(IApiHandler.PROP_API, "id128");
		// serviceInfo = new ServiceInfo(IApiHandler.class.getName(), id128,
		// props);
		// serviceInfoList.add(serviceInfo);
		//
		// props.put(IApiHandler.PROP_API, "id128hex");
		// serviceInfo = new ServiceInfo(IApiHandler.class.getName(),
		// id128Hex, props);
		// serviceInfoList.add(serviceInfo);
		// }
		// return serviceInfoList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		// IdGenerator.disposeInstance(idGen);
		super.stop(bundleContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModuleName() {
		return DaspBundleConstants.MODULE_NAME;
	}

	@Override
	protected String[] getSpringConfigFiles() {
		return new String[] { "/META-INF/osgispring/*.xml" };
	}
}
