package ddth.dasp.framework.osgi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

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
public abstract class BaseSpringBundleActivator extends BaseBundleActivator {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseSpringBundleActivator.class);
    private OsgiBundleXmlApplicationContext applicationContext;

    /**
     * Gets Spring's {@link ApplicationContext} instance.
     * 
     * @return
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets a Spring bean by name.
     * 
     * @param name
     * @return
     */
    protected Object getSpringBean(String name) {
        if (applicationContext == null) {
            return null;
        }
        try {
            Object bean = applicationContext.getBean(name);
            return bean;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets a Spring bean by class.
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    protected <T> T getSpringBean(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        try {
            T bean = applicationContext.getBean(clazz);
            return bean;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets a Spring bean by name and class .
     * 
     * @param <T>
     * @param name
     * @param clazz
     * @return
     */
    protected <T> T getSpringBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        try {
            T bean = applicationContext.getBean(name, clazz);
            return bean;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets list of Spring's configuration files.
     * 
     * Sub-class return <code>null</code> or an empty array to indicate that it
     * does not need Spring's {@link ApplicationContext}.
     * 
     * @return
     */
    protected abstract String[] getSpringConfigFiles();

    protected void initApplicationContext() throws Exception {
        OsgiBundleXmlApplicationContext ac = new OsgiBundleXmlApplicationContext();
        ac.setBundleContext(getBundleContext());
        ac.setPublishContextAsService(false);
        String[] springConfigFiles = getSpringConfigFiles();
        if (springConfigFiles != null && springConfigFiles.length > 0) {
            ac.setConfigLocations(springConfigFiles);
        }
        // ac.refresh();
        ac.normalRefresh();
        ac.start();
        this.applicationContext = ac;
    }

    protected void destroyApplicationContext() throws Exception {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ServiceInfo> getServiceInfoList() {
        List<ServiceInfo> result = super.getServiceInfoList();
        if (result == null) {
            result = new ArrayList<ServiceInfo>();
        }
        Map<String, IServiceAutoRegister> autoBeans = applicationContext
                .getBeansOfType(IServiceAutoRegister.class);
        for (Map.Entry<String, IServiceAutoRegister> entry : autoBeans.entrySet()) {
            IServiceAutoRegister service = entry.getValue();
            ServiceInfo serviceInfo = new ServiceInfo(service.getClassName(), service,
                    service.getProperties());
            result.add(serviceInfo);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalBundleStart(BundleContext bundleContext) throws Exception {
        setBundleContext(bundleContext);
        setBundle(bundleContext.getBundle());

        initApplicationContext();

        super.internalBundleStart(bundleContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalBundleStop(BundleContext bundleContext) throws Exception {
        try {
            destroyApplicationContext();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            super.internalBundleStop(bundleContext);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
