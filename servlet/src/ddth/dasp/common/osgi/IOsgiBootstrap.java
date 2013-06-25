package ddth.dasp.common.osgi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public interface IOsgiBootstrap {

    /**
     * Deploys a bundle from a file.
     * 
     * @param bundleId
     *            String
     * @param file
     *            File
     * @return Bundle
     * @throws BundleException
     * @throws FileNotFoundException
     */
    public Bundle deployBundle(String bundleId, File file) throws FileNotFoundException,
            BundleException;

    /**
     * Deploys a bundle from a URL.
     * 
     * @param bundleId
     *            String
     * @param url
     *            URL
     * @return Bundle
     * @throws BundleException
     * @throws IOException
     */
    public Bundle deployBundle(String bundleId, URL url) throws IOException, BundleException;

    /**
     * Starts an installed bundle.
     * 
     * @param bundle
     *            Bundle
     * @throws BundleException
     */
    public void startBundle(Bundle bundle) throws BundleException;

    /**
     * Gets the {@link BundleContext} instance.
     * 
     * @return BundleContext
     */
    public BundleContext getBundleContext();

    /**
     * Gets a service reference by name.
     * 
     * @param clazz
     *            String
     * @return ServiceReference
     */
    public ServiceReference<?> getServiceReference(String clazz);

    /**
     * Gets a service reference by query. If more than one service reference
     * match the query, this method will return the service reference that has
     * the highest version number.
     * 
     * @param clazz
     *            String
     * @param query
     *            String
     * @return ServiceReference
     * @see http://www.osgi.org/javadoc/r4v43/org/osgi/framework/Filter.html for
     *      query syntax
     */
    public ServiceReference<?> getServiceReference(String clazz, String query);

    /**
     * Gets a service reference by filter. If more than one service reference
     * match the filter, this method will return the service reference that has
     * the highest version number.
     * 
     * @param bundleContext
     * @param clazz
     * @param filter
     * @return
     */
    public ServiceReference<?> getServiceReference(String clazz, Map<String, String> filter);

    /**
     * Gets all service references by query.
     * 
     * @param clazz
     *            String
     * @param query
     *            String
     * @return ServiceReference[]
     * @see http://www.osgi.org/javadoc/r4v43/org/osgi/framework/Filter.html for
     *      query syntax
     */
    public ServiceReference<?>[] getServiceReferences(String clazz, String query);

    /**
     * Gets all service references by filter.
     * 
     * @param bundleContext
     * @param clazz
     * @param filter
     * @return
     */
    public ServiceReference<?>[] getServiceReferences(String clazz, Map<String, String> filter);

    /**
     * "Unget" a get service reference.
     * 
     * @param sref
     *            ServiceReference
     */
    public void ungetServiceReference(ServiceReference<?> sref);

    /**
     * Gets a service from a service reference.
     * 
     * @param sref
     *            ServiceReference
     * @return Object
     */
    public Object getService(ServiceReference<?> sref);

    /**
     * Gets a service from a service reference.
     * 
     * @param <T>
     * @param sref
     *            ServiceReference
     * @param clazz
     *            Class
     * @return T
     */
    public <T> T getService(ServiceReference<T> sref, Class<T> clazz);

    /**
     * Gets an OSGi service. This method unregisters the
     * {@link ServiceReference} so caller does not need to do it.
     * 
     * @param <T>
     * @param clazz
     *            Class<T>
     * @return T
     */
    public <T> T getService(Class<T> clazz);

    /**
     * Gets an OSGi service. This method unregisters the
     * {@link ServiceReference} so caller does not need to do it.
     * 
     * @param <T>
     * @param clazz
     *            Class<T>
     * @param filter
     *            Map<String, String>
     * @return T
     */
    public <T> T getService(Class<T> clazz, Map<String, String> filter);

    /**
     * Gets an OSGi service that matches the query. This method unregisters the
     * {@link ServiceReference} so caller does not need to do it.
     * 
     * @param <T>
     * @param clazz
     *            Class<T>
     * @param query
     *            String
     * @return T
     */
    public <T> T getService(Class<T> clazz, String query);
}
