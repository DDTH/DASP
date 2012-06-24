package ddth.dasp.servlet.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.felix.framework.util.Util;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.OsgiUtils;

public class FelixOsgiBootstrap implements IOsgiBootstrap {

	private final static String OSGI_CONFIG_FILE = "osgi-felix.properties";

	private ServletContext servletContext;
	private String osgiContainerLocation = "/WEB-INF/osgi-container";
	private Logger LOGGER = LoggerFactory.getLogger(FelixOsgiBootstrap.class);
	private Marker FATAL = MarkerFactory.getMarker("FATAL");
	private Framework framework;
	private String remoteShellListenIp = "127.0.0.1";
	private int remoteShellListenPort = 6666;

	protected ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Gets the osgi container folder.
	 * 
	 * @return String
	 */
	protected String getOsgiContainerLocation() {
		return osgiContainerLocation;
	}

	/**
	 * Sets the osgi container folder.
	 * 
	 * @param osgiContainerLocation
	 *            String
	 */
	public void setOsgiContainerLocation(String osgiContainerLocation) {
		this.osgiContainerLocation = osgiContainerLocation;
	}

	/**
	 * Set the Felix's Remote Shell's listen IP.
	 * 
	 * @param remoteShellListenIp
	 *            String
	 */
	public void setRemoteShellListenIp(String remoteShellListenIp) {
		this.remoteShellListenIp = remoteShellListenIp;
	}

	/**
	 * Set the Felix's Remote Shell's listen port.
	 * 
	 * @param remoteShellListenPort
	 *            int
	 */
	public void setRemoteShellListenPort(int remoteShellListenPort) {
		this.remoteShellListenPort = remoteShellListenPort;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BundleContext getBundleContext() {
		return framework.getBundleContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle deployBundle(String bundleId, File file)
			throws FileNotFoundException, BundleException {
		if (file.isFile() & file.canRead()) {
			return deployBundle(bundleId != null ? bundleId : file.getName(),
					new FileInputStream(file));
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bundle deployBundle(String bundleId, URL url)
			throws BundleException, IOException {
		if (url != null) {
			return deployBundle(bundleId, url.openStream());
		}
		return null;
	}

	/**
	 * Starts an installed bundle.
	 * 
	 * @param bundle
	 *            Bundle
	 * @throws BundleException
	 */
	public void startBundle(Bundle bundle) throws BundleException {
		if (bundle == null) {
			LOGGER.warn("Null argument!");
			return;
		}
		int state = bundle.getState();
		if ((state == Bundle.RESOLVED || state == Bundle.INSTALLED)
				&& bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
			bundle.start();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Bundle [" + bundle + "] has been started.");
			}
		}
	}

	/**
	 * Deploys a bundle from an {@link InputStream}. The input stream will be
	 * automatically closed by this method.
	 * 
	 * @param bundleId
	 *            String
	 * @param is
	 *            InputStream
	 * @return Bundle the deployed bundle, or <code>null</code> if not
	 *         successful
	 * @throws BundleException
	 */
	protected Bundle deployBundle(String bundleId, InputStream is)
			throws BundleException {
		return deployBundle(bundleId, is, false);
	}

	/**
	 * Deploys a bundle from an {@link InputStream}. The input stream will be
	 * automatically closed by this method.
	 * 
	 * @param bundleId
	 *            String
	 * @param is
	 *            InputStream
	 * @param start
	 *            boolean specify if the bundle should be automatically started
	 *            after deployment
	 * @return Bundle the deployed bundle, or <code>null</code> if not
	 *         successful
	 * @throws BundleException
	 */
	protected Bundle deployBundle(String bundleId, InputStream is, boolean start)
			throws BundleException {
		try {
			Bundle bundle = framework.getBundleContext().installBundle(
					bundleId, is);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Bundle [" + bundle + "] has been deployed.");
			}
			if (start) {
				startBundle(bundle);
			}
			return bundle;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceReference<?> getServiceReference(String clazz) {
		return OsgiUtils.getServiceReference(getBundleContext(), clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceReference<?> getServiceReference(String clazz, String query) {
		return OsgiUtils.getServiceReference(getBundleContext(), clazz, query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceReference<?> getServiceReference(String clazz,
			Map<String, String> filter) {
		return OsgiUtils.getServiceReference(getBundleContext(), clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceReference<?>[] getServiceReferences(String clazz, String query) {
		return OsgiUtils.getServiceReferences(getBundleContext(), clazz, query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServiceReference<?>[] getServiceReferences(String clazz,
			Map<String, String> filter) {
		return OsgiUtils
				.getServiceReferences(getBundleContext(), clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ungetServiceReference(ServiceReference<?> sref) {
		OsgiUtils.ungetServiceReference(getBundleContext(), sref);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getService(ServiceReference<?> sref) {
		return OsgiUtils.getService(getBundleContext(), sref);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getService(ServiceReference<T> sref, Class<T> clazz) {
		return OsgiUtils.getService(getBundleContext(), sref, clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getService(Class<T> clazz) {
		return OsgiUtils.getService(getBundleContext(), clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getService(Class<T> clazz, Map<String, String> filter) {
		return OsgiUtils.getService(getBundleContext(), clazz, filter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getService(Class<T> clazz, String query) {
		return OsgiUtils.getService(getBundleContext(), clazz, query);
	}

	private File renderOsgiContainerLocation() {
		String root = servletContext.getRealPath("");
		return new File(root, osgiContainerLocation);
	}

	private Properties loadConfigProperties() {
		File configFile = new File(renderOsgiContainerLocation(),
				OSGI_CONFIG_FILE);
		Properties configProps = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(configFile);
			configProps.load(fis);
		} catch (IOException e) {
			LOGGER.error(FATAL, e.getMessage());
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		// perform variables substitution for system properties.
		for (Enumeration<?> e = configProps.propertyNames(); e
				.hasMoreElements();) {
			String name = (String) e.nextElement();
			String value = configProps.getProperty(name);
			value = Util.substVars(value, name, null, configProps);
			configProps.setProperty(name, value);
		}

		// configure Felix auto-deploy directory
		String sAutoDeployDir = configProps
				.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY);
		if (sAutoDeployDir == null) {
			throw new RuntimeException("Can not find configuration ["
					+ AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + "] in file "
					+ configFile.getAbsolutePath());
		}
		File fAutoDeployDir = new File(renderOsgiContainerLocation(),
				sAutoDeployDir);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + ": "
					+ fAutoDeployDir.getAbsolutePath());
		}
		configProps.setProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
				fAutoDeployDir.getAbsolutePath());

		// configure Felix temp (storage) directory
		String sCacheDir = configProps.getProperty(Constants.FRAMEWORK_STORAGE);
		if (sCacheDir == null) {
			throw new RuntimeException("Can not find configuration ["
					+ Constants.FRAMEWORK_STORAGE + "] in file "
					+ configFile.getAbsolutePath());
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(Constants.FRAMEWORK_STORAGE + ": " + sCacheDir);
		}
		// File fCacheDir = new File(System.getProperty("java.io.tmpdir"),
		// sCacheDir);
		// configProps.setProperty(Constants.FRAMEWORK_STORAGE,
		// fCacheDir.getAbsolutePath());

		// configure Felix's File Install watch directory
		String sMonitorDir = configProps.getProperty("felix.fileinstall.dir");
		if (sMonitorDir != null) {
			File fMonitorDir = new File(renderOsgiContainerLocation(),
					sMonitorDir);
			configProps.setProperty("felix.fileinstall.dir", fMonitorDir
					.getAbsolutePath());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("felix.fileinstall.dir: "
						+ fMonitorDir.getAbsolutePath());
			}
		}

		// configure Felix's Remote Shell listen IP & Port
		if (remoteShellListenIp != null) {
			configProps
					.setProperty("osgi.shell.telnet.ip", remoteShellListenIp);
		}
		if (remoteShellListenPort > 0) {
			configProps.setProperty("osgi.shell.telnet.port", String
					.valueOf(remoteShellListenPort));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Remote Shell: " + remoteShellListenIp + ":"
					+ remoteShellListenPort);
		}

		return configProps;
	}

	private void autoDeployBundles(Properties configProps, Framework framework) {
		File dir = new File(configProps
				.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY));
		for (File file : dir.listFiles()) {
			if (file.isDirectory() && !file.getName().startsWith(".")) {
				autoDeployBundles(file, framework);
			}
		}
	}

	private void autoDeployBundles(File dir, Framework framework) {
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Auto deploying bundle [" + file + "]...");
				}
				try {
					// Bundle bundle =
					deployBundle(file.getName(), file);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	private void initFelixFramework() throws BundleException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initialzing Apache Felix Framework, OSGi container ["
					+ osgiContainerLocation + "]...");
		}
		Properties configProps = loadConfigProperties();
		Map<String, String> config = new HashMap<String, String>();
		for (Entry<Object, Object> entry : configProps.entrySet()) {
			config.put(entry.getKey().toString(), entry.getValue().toString());
		}
		FrameworkFactory factory = new org.apache.felix.framework.FrameworkFactory();
		framework = factory.newFramework(config);
		framework.init();
		AutoProcessor.process(configProps, framework.getBundleContext());
		autoDeployBundles(configProps, framework);
		framework.start();
	}

	private void startAllBundles() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Starting all bundles...");
		}
		Bundle[] bundles = framework.getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before Start: " + bundle.getBundleId() + ":"
						+ bundle.getState() + "/" + bundle.getSymbolicName());
			}
			try {
				startBundle(bundle);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("After Start: " + bundle.getBundleId() + ":"
						+ bundle.getState() + "/" + bundle.getSymbolicName());
			}
		}
	}

	// @SuppressWarnings("unused")
	// private void deployCustomBundles() {
	// File dir = new File(renderOsgContainerLocation(), "/mybundles");
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug("Starting all bundles from [" + dir.getAbsolutePath()
	// + "]...");
	// }
	// for (File file : dir.listFiles()) {
	// if (file.isFile() && file.getName().endsWith(".jar")) {
	// try {
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug("Deploying custom bundle [" + file
	// + "]...");
	// }
	// Bundle bundle = deployBundle(file.getName(), file);
	// startBundle(bundle);
	// } catch (Exception e) {
	// LOGGER.error(e.getMessage(), e);
	// }
	// }
	// }
	// }

	public void init() throws Exception {
		initFelixFramework();
		startAllBundles();
		// deployCustomBundles();
	}

	public void destroy() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destroying Apache Felix Framework ["
					+ osgiContainerLocation + "]...");
		}
		try {
			if (framework != null) {
				framework.stop();
				framework.waitForStop(0);
			}
		} catch (Exception e) {
			LOGGER.error(FATAL, e.getMessage(), e);
		}
	}
}
