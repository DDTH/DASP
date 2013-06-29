package ddth.dasp.servlet.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.config.IConfigDao;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.OsgiUtils;

public class FelixOsgiBootstrap implements IOsgiBootstrap {

    private final static String OSGI_CONFIG_FILE = "osgi-felix.properties";
    private final static String SYSPROP_SPRING_PROFILE = "spring.profiles.active";

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
    public Bundle deployBundle(String bundleId, File file) throws FileNotFoundException,
            BundleException {
        if (file.isFile() & file.canRead()) {
            return deployBundle(bundleId != null ? bundleId : file.getName(), new FileInputStream(
                    file));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle deployBundle(String bundleId, URL url) throws BundleException, IOException {
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
    protected Bundle deployBundle(String bundleId, InputStream is) throws BundleException {
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
            Bundle bundle = framework.getBundleContext().installBundle(bundleId, is);
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
    public ServiceReference<?> getServiceReference(String clazz, Map<String, String> filter) {
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
    public ServiceReference<?>[] getServiceReferences(String clazz, Map<String, String> filter) {
        return OsgiUtils.getServiceReferences(getBundleContext(), clazz, filter);
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
        File configFile = new File(renderOsgiContainerLocation(), OSGI_CONFIG_FILE);
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
        IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String instanceRandomStr = df.format(date) + "_"
                + System.getProperty(SYSPROP_SPRING_PROFILE, "") + "_" + idGen.generateId48Hex();
        // perform variables substitution for system properties.
        for (Enumeration<?> e = configProps.propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = configProps.getProperty(name);
            if (value != null) {
                value = value.replace("${random}", instanceRandomStr);
            }
            value = Util.substVars(value, name, null, configProps);
            configProps.setProperty(name, value);
        }

        // configure Felix auto-deploy directory
        String sAutoDeployDir = configProps.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY);
        if (sAutoDeployDir == null) {
            throw new RuntimeException("Can not find configuration ["
                    + AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + "] in file "
                    + configFile.getAbsolutePath());
        }
        File fAutoDeployDir = new File(renderOsgiContainerLocation(), sAutoDeployDir);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY + ": "
                    + fAutoDeployDir.getAbsolutePath());
        }
        configProps.setProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY,
                fAutoDeployDir.getAbsolutePath());

        // configure Felix temp (storage) directory
        String sCacheDir = configProps.getProperty(Constants.FRAMEWORK_STORAGE);
        if (sCacheDir == null) {
            throw new RuntimeException("Can not find configuration [" + Constants.FRAMEWORK_STORAGE
                    + "] in file " + configFile.getAbsolutePath());
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Constants.FRAMEWORK_STORAGE + ": " + sCacheDir);
        }
        File fCacheDir = new File(sCacheDir);
        String contextPath = DaspGlobal.getServletContext().getContextPath();
        if (contextPath.equals("")) {
            contextPath = "_";
        }
        fCacheDir = new File(fCacheDir, contextPath);
        configProps.setProperty(Constants.FRAMEWORK_STORAGE, fCacheDir.getAbsolutePath());

        // configure Felix's File Install watch directory
        String sMonitorDir = configProps.getProperty("felix.fileinstall.dir");
        if (sMonitorDir != null) {
            File fMonitorDir = new File(renderOsgiContainerLocation(), sMonitorDir);
            configProps.setProperty("felix.fileinstall.dir", fMonitorDir.getAbsolutePath());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("felix.fileinstall.dir: " + fMonitorDir.getAbsolutePath());
            }
        }

        // configure Felix's Remote Shell listen IP & Port
        if (remoteShellListenIp != null) {
            configProps.setProperty("osgi.shell.telnet.ip", remoteShellListenIp);
        }
        if (remoteShellListenPort > 0) {
            configProps
                    .setProperty("osgi.shell.telnet.port", String.valueOf(remoteShellListenPort));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Remote Shell: " + remoteShellListenIp + ":" + remoteShellListenPort);
        }

        return configProps;
    }

    private void autoDeployBundles(Properties configProps, Framework framework) {
        // make sure bundles are deployed and started in order!
        File dir = new File(configProps.getProperty(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY));
        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.compareTo(file2);
            }
        });
        for (File file : files) {
            if (file.isDirectory() && !file.getName().startsWith(".")) {
                autoDeployBundles(file, framework);
            }
        }
    }

    private void autoDeployBundles(File dir, Framework framework) {
        // make sure bundles are deployed and started in order!
        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.compareTo(file2);
            }
        });
        for (File file : files) {
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

        class _DaspGlobal extends DaspGlobal {
            public _DaspGlobal(IOsgiBootstrap osgiBootstrap) {
                this.setOsgiBootstrap(osgiBootstrap);
            }
        }
        new _DaspGlobal(this);

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
        Arrays.sort(bundles, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle o1, Bundle o2) {
                long result = o1.getBundleId() - o2.getBundleId();
                return result < 0 ? -1 : (result > 0 ? 1 : 0);
            }
        });
        for (Bundle bundle : bundles) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Before Start: " + bundle.getBundleId() + ":" + bundle.getState()
                        + "/" + bundle.getSymbolicName());
            }
            try {
                startBundle(bundle);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("After Start: " + bundle.getBundleId() + ":" + bundle.getState() + "/"
                        + bundle.getSymbolicName());
            }
        }
    }

    private IConfigDao configDao;

    @SuppressWarnings("unchecked")
    protected void initBundleConfigDao() throws Exception {
        BundleContext bundleContext = getBundleContext();
        String daoClass = bundleContext.getProperty("osgi.dasp.config.dao.class");
        Class<IConfigDao> clazz = (Class<IConfigDao>) Class.forName(daoClass);
        configDao = clazz.newInstance();
        configDao.init(bundleContext);

        bundleContext.registerService(IConfigDao.class, configDao, null);
    }

    protected void destroyBundleConfigDao() throws Exception {
        configDao.destroy(getBundleContext());
    }

    public void init() throws Exception {
        initFelixFramework();
        startAllBundles();
        initBundleConfigDao();
    }

    public void destroy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Destroying Apache Felix Framework [" + osgiContainerLocation + "]...");
        }
        try {
            destroyBundleConfigDao();
        } catch (Exception e) {
            LOGGER.error(FATAL, e.getMessage(), e);
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
