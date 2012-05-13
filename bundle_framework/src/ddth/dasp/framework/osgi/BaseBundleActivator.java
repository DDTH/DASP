package ddth.dasp.framework.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import ddth.dasp.utils.OsgiUtils;

public abstract class BaseBundleActivator implements BundleActivator {

	private final Logger LOGGER = LoggerFactory
			.getLogger(BaseBundleActivator.class);
	private BundleContext bundleContext;
	private Bundle bundle;
	private OsgiBundleXmlApplicationContext applicationContext;
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

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Gets an OSGi service.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	protected <T> T getService(Class<T> clazz) {
		ServiceReference sref = OsgiUtils.getServiceReference(bundleContext,
				clazz.getName());
		if (sref != null) {
			try {
				return OsgiUtils.getService(bundleContext, sref, clazz);
			} finally {
				OsgiUtils.ungetServiceReference(bundleContext, sref);
			}
		}
		return null;
	}

	/**
	 * Init method: Initializes a {@link Properties} for latter use.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Properties initProperties() throws Exception {
		Properties props = new Properties();
		String version = (String) bundle.getHeaders().get(
				Constants.BUNDLE_VERSION);
		props.put("Version", version);
		return props;
	}

	/**
	 * Init method: Initializes Spring's {@link ApplicationContext}.
	 * 
	 * @throws Exception
	 */
	protected void initApplicationContext() throws Exception {
		if (this.applicationContext == null) {
			String[] springConfigFiles = new String[] { "META-INF/osgispring/*.xml" };
			OsgiBundleXmlApplicationContext ac = new OsgiBundleXmlApplicationContext();
			ac.setBundleContext(bundleContext);
			ac.setPublishContextAsService(false);
			ac.setConfigLocations(springConfigFiles);
			ac.refresh();
			ac.start();
			this.applicationContext = ac;
		}
	}

	/**
	 * Destroy method: Destroys the Spring's {@link ApplicationContext}
	 * 
	 * @throws Exception
	 */
	protected void destroyApplicationContext() throws Exception {
		if (applicationContext != null) {
			try {
				applicationContext.close();
			} finally {
				applicationContext = null;
			}
		}
	}

	/**
	 * Loads a resource by location.
	 * 
	 * @param location
	 * @return
	 * @throws IOException
	 */
	protected InputStream loadResource(String location) throws IOException {
		return OsgiUtils.loadBundleResource(bundle, location);
	}

	/**
	 * Finds all resources from a location.
	 * 
	 * @param location
	 * @return
	 * @throws IOException
	 */
	protected String[] enumResources(String location) throws IOException {
		return OsgiUtils.enumResources(bundle, location);
	}

	/**
	 * Init method: Loads and registers bundle's language packs.
	 * 
	 * @throws Exception
	 */
	protected void registerLanguages() throws Exception {
		// EMPTY
	}

	/**
	 * Destroy method: Unregisters bundle's language packs.
	 * 
	 * @throws Exception
	 */
	protected void unregisterLanguages() throws Exception {
		// EMPTY
	}

	/**
	 * Sub-class overrides this method to register its own services.
	 * 
	 * @param props
	 *            Properties
	 */
	protected void registerServices(Properties props) {
		// EMPTY
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

	/**
	 * Unregisters registered services.
	 * 
	 * This method is automatically called by {@link #stop(BundleContext)}.
	 * Services registered via
	 * {@link #registerService(String, Object, Properties)} will be
	 * unregistered.
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
	 * Init method: Initializes main menu's items.
	 * 
	 * @throws Exception
	 */
	protected void initMainMenu() throws Exception {
		// EMPTY
	}

	/**
	 * Init method: Initializes side menu's items.
	 * 
	 * @throws Exception
	 */
	protected void initSideMenu() throws Exception {
		// EMPTY
	}

	/**
	 * Destroy method: Destroys main menu items.
	 * 
	 * @throws Exception
	 */
	protected void destroyMainMenu() throws Exception {
		// EMPTY
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		setBundleContext(bundleContext);
		setBundle(bundleContext.getBundle());

		try {
			Properties props = initProperties();
			initApplicationContext();
			registerLanguages();
			registerServices(props);
			initMainMenu();
			initSideMenu();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) {
		try {
			destroyMainMenu();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}

		try {
			unregisterServices();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}

		try {
			unregisterLanguages();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}

		try {
			destroyApplicationContext();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}
}
