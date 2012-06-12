package ddth.dasp.common.osgi;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;

public abstract class BaseBundleActivator implements BundleActivator {

	private final Logger LOGGER = LoggerFactory
			.getLogger(BaseBundleActivator.class);
	private BundleContext bundleContext;
	private Bundle bundle;
	private Properties properties;
	private List<ServiceRegistration> registeredServices = new LinkedList<ServiceRegistration>();

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

		Properties props = new Properties();
		String version = (String) bundle.getHeaders().get(
				Constants.BUNDLE_VERSION);
		props.put("Version", version);
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

	protected List<Object[]> getServiceInfoList() {
		return null;
	}

	/**
	 * Registers OSGi services provided by this module.
	 */
	protected void registerServices() {
		List<Object[]> serviceInfoList = getServiceInfoList();
		if (serviceInfoList == null) {
			return;
		}
		for (Object[] serviceInfo : serviceInfoList) {
			String serviceName = serviceInfo[0].toString();
			Object serviceObj = serviceInfo[1];
			registerService(serviceName, serviceObj, getProperties());
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
		for (ServiceRegistration sr : registeredServices) {
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
	 * @param name
	 *            String
	 * @param service
	 *            Object
	 * @param props
	 *            properties
	 * @return ServiceRegistration
	 */
	protected ServiceRegistration registerService(String name, Object service,
			Properties props) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registering service [" + name + "] with properties "
					+ props);
		}
		ServiceRegistration sr = bundleContext.registerService(name, service,
				props);
		if (sr != null) {
			registeredServices.add(sr);
		}
		return sr;
	}
}
