package ddth.dasp.framework.osgi.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import ddth.dasp.framework.osgi.BundleResourceLoader;
import freemarker.cache.TemplateLoader;

/**
 * This FreeMarker's {@link TemplateLoader} uses {@link BundleResourceLoader} to
 * load resources.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundleTemplateLoader implements TemplateLoader {

	private BundleResourceLoader bundleResourceLoader;
	private String prefix, suffix;

	protected BundleResourceLoader getBundleResourceLoader() {
		return bundleResourceLoader;
	}

	public void setBundleResourceLoader(
			BundleResourceLoader bundleResourceLoader) {
		this.bundleResourceLoader = bundleResourceLoader;
	}

	protected String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Builds template resource's full path.
	 * 
	 * @param name
	 * @return
	 */
	protected String buildPath(String name) {
		String path = name;
		if (prefix != null) {
			path = prefix + name;
		}
		if (suffix != null) {
			path += suffix;
		}
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		// empty since there is no thing to "close"
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object findTemplateSource(String name) throws IOException {
		return bundleResourceLoader.resourceExists(buildPath(name)) ? name
				: null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLastModified(Object templateSource) {
		return bundleResourceLoader.getLastModified(buildPath(templateSource
				.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		String resourceContent = bundleResourceLoader.loadResourceAsString(
				buildPath(templateSource.toString()), encoding);
		return new StringReader(resourceContent);
	}
}
