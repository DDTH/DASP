package ddth.dasp.framework.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

/**
 * This class supports loading resources within a bundle.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundleResourceLoader implements BundleContextAware,
		ApplicationContextAware {
	private Bundle bundle;

	protected Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Gets a resource's last modified timestamp.
	 * 
	 * @param path
	 * @return
	 */
	public long getLastModified(String path) {
		return bundle.getLastModified();
	}

	/**
	 * Checks if a resource exists within the bundle.
	 * 
	 * @param path
	 * @return
	 */
	public boolean resourceExists(String path) {
		return bundle.getEntry(path) != null;
	}

	/**
	 * Loads a resource within the bundle.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public InputStream loadResource(String path) throws IOException {
		URL url = bundle.getEntry(path);
		return url.openStream();
	}

	/**
	 * Load a resource content as byte array.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public byte[] loadResourceAsBinary(String path) throws IOException {
		if (!resourceExists(path)) {
			return null;
		}
		InputStream is = loadResource(path);
		try {
			return IOUtils.toByteArray(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Load a resource content as a string, using the default encoding.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String loadResourceAsString(String path) throws IOException {
		if (!resourceExists(path)) {
			return null;
		}
		byte[] data = loadResourceAsBinary(path);
		return new String(data);
	}

	/**
	 * Load a resource content as a string, using the specified encoding.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String loadResourceAsString(String path, String encoding)
			throws IOException {
		if (!resourceExists(path)) {
			return null;
		}
		byte[] data = loadResourceAsBinary(path);
		return IOUtils.toString(data, encoding);
	}

	/**
	 * Gets list of entries under a root path.
	 * 
	 * @param rootPath
	 * @return
	 */
	public String[] getEntryPaths(String rootPath) {
		List<String> result = new ArrayList<String>();
		Enumeration<String> paths = bundle.getEntryPaths(rootPath);
		if (paths == null) {
			return null;
		}
		while (paths.hasMoreElements()) {
			String path = paths.nextElement();
			result.add(path);
		}
		return result.toArray(new String[0]);
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		if (bundle == null) {
			setBundle(bundleContext.getBundle());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		if (bundle == null) {
			OsgiBundleXmlApplicationContext osgiAc = (OsgiBundleXmlApplicationContext) ac;
			setBundle(osgiAc.getBundle());
		}
	}
}
