package ddth.dasp.framework.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * This {@link IResourceLoader} loads resources from file system.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class FsResourceLoader extends AbstractResourceLoader {

	private String rootDir;
	private String envNameRootDir;

	protected String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	/**
	 * If {@link FsResourceLoader#envNameRootDir} is set, value of the
	 * corresponding environment (if set) will be assigned to {@link #rootDir}.
	 * 
	 * @return
	 */
	protected String getEnvNameRootDir() {
		return envNameRootDir;
	}

	/**
	 * If {@link FsResourceLoader#envNameRootDir} is set, value of the
	 * corresponding environment (if set) will be assigned to {@link #rootDir}.
	 * 
	 * @param envNameRootDir
	 */
	public void setEnvNameRootDir(String envNameRootDir) {
		this.envNameRootDir = envNameRootDir;
	}

	public void init() {
		if (!StringUtils.isBlank(envNameRootDir)) {
			String value = System.getProperty(envNameRootDir);
			if (!StringUtils.isBlank(value)) {
				setRootDir(value);
			}
		}
	}

	public void destroy() {
		// EMPTY
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLastModified(String path) {
		File file = StringUtils.isBlank(rootDir) ? new File(path) : new File(
				rootDir, path);
		return file.lastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean resourceExists(String path) {
		File file = StringUtils.isBlank(rootDir) ? new File(path) : new File(
				rootDir, path);
		return file.exists();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream loadResource(String path) throws IOException {
		File file = StringUtils.isBlank(rootDir) ? new File(path) : new File(
				rootDir, path);
		if (file.exists()) {
			InputStream is = new FileInputStream(file);
			return is;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getEntryPaths(String rootPath) throws IOException {
		File file = StringUtils.isBlank(rootDir) ? new File(rootPath)
				: new File(rootDir, rootPath);
		if (file.isFile()) {
			return new String[] { rootPath };
		}
		return file.list();
	}
}
