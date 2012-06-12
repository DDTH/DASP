package ddth.dasp.servlet.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link ServletContextListener} is responsible for initializing and
 * destroying the context's temp directory.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ContextTempDirListener implements ServletContextListener {

	private Logger LOGGER = LoggerFactory
			.getLogger(ContextTempDirListener.class);
	private static File CTX_TMP_DIR;

	public static File getTempDir() {
		return CTX_TMP_DIR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deleting context temp dir [" + CTX_TMP_DIR + "]...");
		}
		FileUtils.deleteQuietly(CTX_TMP_DIR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String systemTempDir = System.getProperty("java.io.tmpdir");
		String randomStr = "CTX_" + RandomStringUtils.randomAlphanumeric(16);
		CTX_TMP_DIR = new File(systemTempDir, randomStr);
		CTX_TMP_DIR.mkdirs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created context temp dir [" + CTX_TMP_DIR + "].");
		}
	}
}
