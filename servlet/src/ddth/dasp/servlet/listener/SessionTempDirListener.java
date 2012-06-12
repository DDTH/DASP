package ddth.dasp.servlet.listener;

import java.io.File;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.utils.DaspConstants;

/**
 * This {@link ServletContextListener} is responsible for initializing and
 * destroying the context's temp directory.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class SessionTempDirListener implements HttpSessionListener {

	private Logger LOGGER = LoggerFactory
			.getLogger(SessionTempDirListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		File contextTempDir = ContextTempDirListener.getTempDir();
		String randomStr = "SESSION_"
				+ RandomStringUtils.randomAlphanumeric(16);
		File sessionTempDir = new File(contextTempDir, randomStr);
		sessionTempDir.mkdirs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created session temp dir [" + sessionTempDir + "].");
		}
		event.getSession().setAttribute(DaspConstants.SESSION_SESSION_TEMP_DIR,
				sessionTempDir);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		Object tmp = event.getSession().getAttribute(
				DaspConstants.SESSION_SESSION_TEMP_DIR);
		if (tmp instanceof File) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting session temp dir [" + tmp + "]...");
			}
			FileUtils.deleteQuietly((File) tmp);
		}
	}
}
