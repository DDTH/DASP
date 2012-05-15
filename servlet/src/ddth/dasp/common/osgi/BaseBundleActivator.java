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
		setProperties(props);

		registerServices();
		registerModule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		unregisterModule();
		unregisterServices();
	}

	/**
	 * Gets the module's name.
	 * 
	 * Returns <code>null</code> if the bundle does not wish to register for a
	 * module.
	 * 
	 * @return
	 */
	protected String getModuleName() {
		return null;
	}

	/**
	 * Gets the module's SpringMVC's {@link HandlerMapping}.
	 * 
	 * Returns <code>null</code> if the bundle does not wish to register for a
	 * module.
	 * 
	 * @return
	 */
	protected HandlerMapping getSpringMvcHandlerMapping() {
		return null;
	}

	/**
	 * Gets the module's SpringMVC's {@link ViewResolver}.
	 * 
	 * Returns <code>null</code> if the bundle does not wish to register for a
	 * {@link ViewResolver}.
	 * 
	 * @return
	 */
	protected ViewResolver getSpringMvcViewResolver() {
		return null;
	}

	/**
	 * Register the application module.
	 */
	protected void registerModule() {
		String moduleName = getModuleName();
		HandlerMapping handlerMapping = getSpringMvcHandlerMapping();
		if (!StringUtils.isEmpty(moduleName) && handlerMapping != null) {
			Properties props = new Properties();
			props.putAll(getProperties());
			props.put("Module", moduleName);

			registerService(HandlerMapping.class.getName(), handlerMapping,
					props);

			ViewResolver viewResolver = getSpringMvcViewResolver();
			if (viewResolver != null) {
				registerService(ViewResolver.class.getName(), handlerMapping,
						props);
			}
		}
	}

	/**
	 * Unregister the application module.
	 */
	protected void unregisterModule() {
		// EMPTY
	}

	/**
	 * Registers OSGi services provided by this module.
	 * 
	 * This method does nothing. Sub-class overrides this method to register its
	 * own services.
	 */
	protected void registerServices() {
		// EMPTY
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
				sr.unregister();
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
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
