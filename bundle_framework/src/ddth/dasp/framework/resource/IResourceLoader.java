package ddth.dasp.framework.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that provides APIs to load resources.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IResourceLoader {
	/**
	 * Gets a resource's last modified timestamp.
	 * 
	 * @param path
	 * @return
	 */
	public long getLastModified(String path);

	/**
	 * Checks if a resource exists.
	 * 
	 * @param path
	 * @return
	 */
	public boolean resourceExists(String path);

	/**
	 * Loads a resource specified by the path.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public InputStream loadResource(String path) throws IOException;

	/**
	 * Load a resource content as byte array.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public byte[] loadResourceAsBinary(String path) throws IOException;

	/**
	 * Load a resource content as a string, using the default encoding.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String loadResourceAsString(String path) throws IOException;

	/**
	 * Load a resource content as a string, using the specified encoding.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String loadResourceAsString(String path, String encoding)
			throws IOException;

	/**
	 * Gets list of entries under a root path.
	 * 
	 * @param rootPath
	 * @return
	 */
	public String[] getEntryPaths(String rootPath) throws IOException;
}
