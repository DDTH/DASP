package ddth.dasp.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.utils.VersionUtils;

/**
 * OSGi related utilities.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class OsgiUtils {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(OsgiUtils.class);

	/**
	 * Obtains an OSGi service reference.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            String
	 * @return ServiceReference
	 */
	public static ServiceReference getServiceReference(
			BundleContext bundleContext, String clazz) {
		return getServiceReference(bundleContext, clazz, (String) null);
	}

	/**
	 * Obtains an OSGi service reference.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            String
	 * @param filter
	 *            Map<String, String>
	 * @return ServiceReference
	 */
	public static ServiceReference getServiceReference(
			BundleContext bundleContext, String clazz,
			Map<String, String> filter) {
		if (filter == null || filter.size() == 0) {
			return getServiceReference(bundleContext, clazz, (String) null);
		}
		StringBuilder query = new StringBuilder();
		for (Entry<String, String> entry : filter.entrySet()) {
			query.append("(");
			query.append(entry.getKey());
			query.append("=");
			query.append(entry.getValue());
			query.append(")");
		}
		return getServiceReference(bundleContext, clazz, query.toString());
	}

	/**
	 * Obtains an OSGi service reference.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            String
	 * @param query
	 *            String
	 * @return ServiceReference
	 */
	public static ServiceReference getServiceReference(
			BundleContext bundleContext, String clazz, String query) {
		ServiceReference[] refs = getServiceReferences(bundleContext, clazz,
				query);
		ServiceReference ref = (refs != null && refs.length > 0) ? refs[0]
				: null;
		for (int i = 1, n = refs != null ? refs.length : 0; i < n; i++) {
			ServiceReference temp = refs[i];
			Object v1 = ref.getProperty("Version");
			Object v2 = temp.getProperty("Version");
			if (VersionUtils.compareVersions(v1 != null ? v1.toString() : null,
					v2 != null ? v2.toString() : null) < 0) {
				// unget unmatched service reference
				ungetServiceReference(bundleContext, ref);
				ref = temp;
			} else {
				// unget unmatched service reference
				ungetServiceReference(bundleContext, temp);
			}
		}
		return ref;
	}

	/**
	 * Obtains all OSGi service references by filter.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            String
	 * @param filter
	 *            Map<String, String>
	 * @return ServiceReference
	 */
	public static ServiceReference[] getServiceReferences(
			BundleContext bundleContext, String clazz,
			Map<String, String> filter) {
		if (filter == null || filter.size() == 0) {
			return getServiceReferences(bundleContext, clazz, (String) null);
		}
		StringBuilder query = new StringBuilder();
		for (Entry<String, String> entry : filter.entrySet()) {
			query.append("(");
			query.append(entry.getKey());
			query.append("=");
			query.append(entry.getValue());
			query.append(")");
		}
		return getServiceReferences(bundleContext, clazz, query.toString());
	}

	/**
	 * Obtains all OSGi service references by query.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            String
	 * @param query
	 *            String
	 * @return ServiceReference[]
	 */
	public static ServiceReference[] getServiceReferences(
			BundleContext bundleContext, String clazz, String query) {
		if (query == null) {
			ServiceReference serviceRef = bundleContext
					.getServiceReference(clazz);
			return serviceRef != null ? new ServiceReference[] { serviceRef }
					: new ServiceReference[0];
		} else {
			try {
				ServiceReference[] serviceRefs = bundleContext
						.getServiceReferences(clazz, query);
				return serviceRefs != null ? serviceRefs
						: new ServiceReference[0];
			} catch (InvalidSyntaxException e) {
				LOGGER.error("Can not get service reference [" + clazz + "/"
						+ query + "]: " + e.getMessage(), e);
				return null;
			}
		}
	}

	/**
	 * Gets the service instance from the service reference.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param sref
	 *            ServiceReference
	 * @return Object
	 */
	public static Object getService(BundleContext bundleContext,
			ServiceReference sref) {
		if (sref != null) {
			return bundleContext.getService(sref);
		}
		return null;
	}

	/**
	 * Gets the service instance from the service reference.
	 * 
	 * @param <T>
	 * @param bundleContext
	 *            BundleContext
	 * @param sref
	 *            ServiceReference
	 * @param clazz
	 *            clazz
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getService(BundleContext bundleContext,
			ServiceReference sref, Class<T> clazz) {
		Object service = getService(bundleContext, sref);
		if (service != null && clazz.isAssignableFrom(service.getClass())) {
			return (T) service;
		}
		return null;
	}

	/**
	 * "Ungets" a service reference.
	 * 
	 * @param bundleContext
	 *            BundleContext
	 * @param sref
	 *            ServiceReference
	 */
	public static void ungetServiceReference(BundleContext bundleContext,
			ServiceReference sref) {
		if (sref != null) {
			bundleContext.ungetService(sref);
		}
	}

	/**
	 * Gets an OSGi service. This method unregisters the
	 * {@link ServiceReference} so caller does not need to do it.
	 * 
	 * @param <T>
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            Class<T>
	 * @return T
	 */
	public static <T> T getService(BundleContext bundleContext, Class<T> clazz) {
		ServiceReference sref = OsgiUtils.getServiceReference(bundleContext,
				clazz.getName());
		if (sref != null) {
			try {
				return getService(bundleContext, sref, clazz);
			} finally {
				ungetServiceReference(bundleContext, sref);
			}
		}
		return null;
	}

	/**
	 * Gets an OSGi service. This method unregisters the
	 * {@link ServiceReference} so caller does not need to do it.
	 * 
	 * @param <T>
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            Class<T>
	 * @param filter
	 *            Map<String, String>
	 * @return T
	 */
	public static <T> T getService(BundleContext bundleContext, Class<T> clazz,
			Map<String, String> filter) {
		ServiceReference sref = OsgiUtils.getServiceReference(bundleContext,
				clazz.getName(), filter);
		if (sref != null) {
			try {
				return getService(bundleContext, sref, clazz);
			} finally {
				ungetServiceReference(bundleContext, sref);
			}
		}
		return null;
	}

	/**
	 * Gets an OSGi service. This method unregisters the
	 * {@link ServiceReference} so caller does not need to do it.
	 * 
	 * @param <T>
	 * @param bundleContext
	 *            BundleContext
	 * @param clazz
	 *            Class<T>
	 * @param query
	 *            String
	 * @return T
	 */
	public static <T> T getService(BundleContext bundleContext, Class<T> clazz,
			String query) {
		ServiceReference sref = OsgiUtils.getServiceReference(bundleContext,
				clazz.getName(), query);
		if (sref != null) {
			try {
				return getService(bundleContext, sref, clazz);
			} finally {
				ungetServiceReference(bundleContext, sref);
			}
		}
		return null;
	}

	/**
	 * Finds all resources from a location.
	 * 
	 * @param bundleContext
	 * @param resourceLocation
	 * @return
	 * @throws IOException
	 */
	public static String[] enumResources(BundleContext bundleContext,
			String resourceLocation) throws IOException {
		return enumResources(bundleContext.getBundle(), resourceLocation);
	}

	/**
	 * Finds all resources from a location.
	 * 
	 * @param bundle
	 * @param resourceLocation
	 * @return
	 * @throws IOException
	 */
	public static String[] enumResources(Bundle bundle, String resourceLocation)
			throws IOException {
		List<String> result = new ArrayList<String>();
		Enumeration<?> e = bundle.getEntryPaths(resourceLocation);
		if (e != null) {
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				result.add(obj.toString());
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * Loads a resource located within the bundle.
	 * 
	 * @param bundleContext
	 * @param resourceLocation
	 * @return
	 * @throws IOException
	 */
	public static InputStream loadBundleResource(BundleContext bundleContext,
			String resourceLocation) throws IOException {
		return loadBundleResource(bundleContext.getBundle(), resourceLocation);
	}

	/**
	 * Loads a resource located within the bundle.
	 * 
	 * @param bundle
	 * @param resourceLocation
	 * @return
	 * @throws IOException
	 */
	public static InputStream loadBundleResource(Bundle bundle,
			String resourceLocation) throws IOException {
		URL url = bundle.getEntry(resourceLocation);
		return url != null ? url.openStream() : null;
	}
}
