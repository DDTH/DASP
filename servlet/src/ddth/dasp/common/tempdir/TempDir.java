package ddth.dasp.common.tempdir;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempDir {

	private final static Logger LOGGER = LoggerFactory.getLogger(TempDir.class);
	private File tempDir;
	private boolean created = false;

	public TempDir(String parent, String name) {
		tempDir = new File(parent, name);
	}

	public TempDir(File parent, String name) {
		tempDir = new File(parent, name);
	}

	public TempDir(TempDir parent, String name) {
		tempDir = new File(parent.tempDir, name);
	}

	@Override
	public String toString() {
		return tempDir.toString();
	}

	/**
	 * Gets the tempdir instance. This method will try to create the tempdir if
	 * it does not exist.
	 * 
	 * @return
	 */
	public File get() {
		if (!tempDir.exists()) {
			create();
		}
		return tempDir;
	}

	/**
	 * Try to create the tempdir (including any necessary but nonexistent parent
	 * directories).
	 * 
	 * @return
	 */
	public boolean create() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating tempdir [" + tempDir + "]...");
		}
		created = true;
		return tempDir.mkdirs();
	}

	/**
	 * Deletes the tempdir.
	 */
	public void delete() {
		if (!created) {
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deleting tempdir [" + tempDir + "]...");
		}
		created = false;
		FileUtils.deleteQuietly(tempDir);
	}
}
