package ddth.dasp.framework.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;

import ddth.dasp.common.osgi.IBundleAwareService;
import ddth.dasp.common.osgi.IRequireCleanupService;

/**
 * This {@link BundleActivator} does the following:
 * <ul>
 * <li>On bundle start:
 * <ul>
 * <li>Saves instance of bundle context and bundle for latter use. See
 * {@link #setBundleContext(BundleContext)} and {@link #setBundle(Bundle)}.</li>
 * <li>Builds bundle's properties. See {@link #setProperties(Properties)}.</li>
 * <li>Calls {@link #registerSpringMvcHandlerMapping()} and
 * {@link #registerSpringMvcViewResolver()}.</li>
 * <li>Calls {@link #registerServices()}.</li>
 * </ul>
 * </li>
 * <li>On bundle stop:
 * <ul>
 * <li>Calls {@link #unregisterServices()}.</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class BaseBundleActivator implements BundleActivator {

	private final Logger LOGGER = LoggerFactory
			.getLogger(BaseBundleActivator.class);
	private BundleContext bundleContext;
	private Bundle bundle;
	private Properties properties;
	private List<ServiceRegistration<?>> registeredServices = new LinkedList<ServiceRegistration<?>>();

	/**
	 * Gets name of the associated bundle.
	 * 
	 * This method returns the bundle's symbolic name.
	 * 
	 * @return
	 */
	public String getBundleName() {
		return bundle.getSymbolicName();
	}

	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	protected void setBundleContext(BundleContext bundeContext) {
		this.bundleContext = bundeContext;
	}

	protected Bundle getBundle() {
		return bundle;
	}

	protected void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	protected Properties getProperties() {
		return properties;
	}

	protected void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		setBundleContext(bundleContext);
		setBundle(bundleContext.getBundle());

		long myId = this.bundle.getBundleId();
		String myName = this.bundle.getSymbolicName();
		Bundle[] currentBundles = bundleContext.getBundles();
		for (Bundle bundle : currentBundles) {
			if (myId != bundle.getBundleId()
					&& myName.equals(bundle.getSymbolicName())) {
				// found another version of me
				handlerAnotherVersionAtStartup(bundle);
			}
		}

		Properties props = new Properties();
		// String version = (String) bundle.getHeaders().get(
		// Constants.BUNDLE_VERSION);
		props.put("Version", bundle.getVersion().toString());
		String moduleName = getModuleName();
		if (!StringUtils.isEmpty(moduleName)) {
			props.put("Module", moduleName);
		}
		setProperties(props);

		registerSpringMvcHandlerMapping();
		registerSpringMvcViewResolver();

		registerServices();
	}

	/**
	 * Called when another version of this bundle is found in the bundle
	 * context.
	 * 
	 * This method stops the other bundle if it's an old version of the current
	 * bundle. Sub-class may override this method to implement its own business
	 * logic.
	 * 
	 * @param bundle
	 * @throws BundleException
	 */
	protected void handlerAnotherVersionAtStartup(Bundle bundle)
			throws BundleException {
		Version myVersion = this.bundle.getVersion();
		Version otherVersion = bundle.getVersion();
		if (myVersion.compareTo(otherVersion) > 0) {
			String msg = "Found an older version of me [" + bundle
					+ "], stopping it!";
			LOGGER.info(msg);
			bundle.stop();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		unregisterServices();
	}

	/**
	 * Registers module's Spring's {@link HandlerMapping}.
	 */
	protected void registerSpringMvcHandlerMapping() {
		String moduleName = getModuleName();
		HandlerMapping handlerMapping = getSpringMvcHandlerMapping();
		if (StringUtils.isEmpty(moduleName) || handlerMapping == null) {
			return;
		}
		registerService(HandlerMapping.class.getName(), handlerMapping,
				getProperties());
	}

	/**
	 * Registers module's Spring's {@link ViewResolver}.
	 */
	protected void registerSpringMvcViewResolver() {
		String moduleName = getModuleName();
		ViewResolver viewResolver = getSpringMvcViewResolver();
		if (StringUtils.isEmpty(moduleName) || viewResolver == null) {
			return;
		}
		registerService(ViewResolver.class.getName(), viewResolver,
				getProperties());
	}

	/**
	 * Gets the module's name.
	 * 
	 * This method simply returns <code>null</code>.
	 * 
	 * @return
	 */
	protected String getModuleName() {
		return null;
	}

	/**
	 * Gets the module's SpringMVC's {@link HandlerMapping}.
	 * 
	 * This method simply returns <code>null</code>.
	 * 
	 * @return
	 */
	protected HandlerMapping getSpringMvcHandlerMapping() {
		return null;
	}

	/**
	 * Gets the module's SpringMVC's {@link ViewResolver}.
	 * 
	 * This method simply returns <code>null</code>.
	 * 
	 * @return
	 */
	protected ViewResolver getSpringMvcViewResolver() {
		return null;
	}

	protected List<ServiceInfo> getServiceInfoList() {
		return null;
	}

	/**
	 * Registers OSGi services provided by this module.
	 */
	protected void registerServices() {
		List<ServiceInfo> serviceInfoList = getServiceInfoList();
		if (serviceInfoList == null || serviceInfoList.size() == 0) {
			return;
		}
		for (ServiceInfo serviceInfo : serviceInfoList) {
			Properties props = new Properties();
			props.putAll(getProperties());
			props.putAll(serviceInfo.getProperties());
			registerService(serviceInfo.getClassName(),
					serviceInfo.getService(), props);
		}
	}

	/**
	 * Unregisters registered services. Services registered via
	 * {@link #registerService(String, Object, Properties)} will be
	 * unregistered.
	 * 
	 * This method is automatically called by {@link #stop(BundleContext)}.
	 * 
	 */
	protected void unregisterServices() {
		for (ServiceRegistration<?> sr : registeredServices) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Unregistering service [" + sr + "]...");
				}
				Object service = bundleContext.getService(sr.getReference());
				if (service instanceof IRequireCleanupService) {
					((IRequireCleanupService) service).destroy();
				}
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
			} finally {
				sr.unregister();
			}
		}
	}

	/**
	 * Convenient method for sub-class to register one service.
	 * 
	 * @param className
	 *            String
	 * @param service
	 *            Object
	 * @param props
	 *            properties
	 * @return ServiceRegistration
	 */
	protected ServiceRegistration<?> registerService(String className,
			Object service, Properties props) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registering service [" + className
					+ "] with properties " + props);
		}
		if (service instanceof IBundleAwareService) {
			((IBundleAwareService) service).setBundle(bundle);
		}
		// ServiceRegistration sr = bundleContext.registerService(name, service,
		// props);
		Dictionary<String, Object> dProps = new Hashtable<String, Object>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			dProps.put(entry.getKey().toString(), entry.getValue());
		}
		ServiceRegistration<?> sr = bundleContext.registerService(className,
				service, dProps);
		if (sr != null) {
			registeredServices.add(sr);
		}
		return sr;
	}
}
