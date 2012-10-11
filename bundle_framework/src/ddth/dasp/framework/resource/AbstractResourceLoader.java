package ddth.dasp.framework.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * An abstract implementation of {@link IResourceLoader}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class AbstractResourceLoader implements IResourceLoader {
	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
	public String loadResourceAsString(String path) throws IOException {
		if (!resourceExists(path)) {
			return null;
		}
		byte[] data = loadResourceAsBinary(path);
		return new String(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String loadResourceAsString(String path, String encoding)
			throws IOException {
		if (!resourceExists(path)) {
			return null;
		}
		byte[] data = loadResourceAsBinary(path);
		return IOUtils.toString(data, encoding);
	}
}
