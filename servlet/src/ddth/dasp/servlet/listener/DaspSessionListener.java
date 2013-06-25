package ddth.dasp.servlet.listener;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.common.utils.DaspConstants;

/**
 * This {@link ServletContextListener} is responsible for initializing and
 * destroying the context's temp directory.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DaspSessionListener implements HttpSessionListener {

    private Logger LOGGER = LoggerFactory.getLogger(DaspSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        initTempDir(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        destroyTempDir(session);
    }

    private void destroyTempDir(HttpSession session) {
        Object tmp = session.getAttribute(DaspConstants.SESSION_SESSION_TEMP_DIR);
        if (tmp instanceof TempDir) {
            try {
                ((TempDir) tmp).delete();
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }

    private void initTempDir(HttpSession session) {
        TempDir contextTempDir = DaspGlobal.getContextTempDir();
        String randomStr = "SESSION_" + RandomStringUtils.randomAlphanumeric(16);
        TempDir sessionTempDir = new TempDir(contextTempDir, randomStr);
        session.setAttribute(DaspConstants.SESSION_SESSION_TEMP_DIR, sessionTempDir);
    }

}
