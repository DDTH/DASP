package ddth.dasp.servlet.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.servlet.osgi.FelixOsgiBootstrap;
import ddth.dasp.servlet.utils.NetUtils;

/**
 * This {@link ServletContextListener} is responsible for initializing and
 * destroying the context's temp directory.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DaspContextListener implements ServletContextListener {

    private Logger LOGGER = LoggerFactory.getLogger(DaspContextListener.class);
    private static TempDir CTX_TMP_DIR;
    private static FelixOsgiBootstrap OSGI_BOOTSTRAP;

    public static TempDir getTempDir() {
        return CTX_TMP_DIR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        destroyOsgiBootstrap();
        detroyTempDir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("SERVLET_CONTEXT", event.getServletContext());
        new _DaspGlobal(data);

        initTempDir(event.getServletContext());
        initOsgiBootstrap(event.getServletContext());
    }

    private void destroyOsgiBootstrap() {
        try {
            OSGI_BOOTSTRAP.destroy();
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage(), t);
        }
    }

    private void initOsgiBootstrap(ServletContext servletContext) {
        OSGI_BOOTSTRAP = new FelixOsgiBootstrap();
        String osgiRemoteShellListenIp = servletContext.getInitParameter("osgiRemoteShellListenIp");
        String osgiRemoteShellListenPort = servletContext
                .getInitParameter("osgiRemoteShellListenPort");
        String osgiContainerLocation = servletContext.getInitParameter("osgiContainerLocation");

        if (!StringUtils.isBlank(osgiRemoteShellListenIp)) {
            OSGI_BOOTSTRAP.setRemoteShellListenIp(osgiRemoteShellListenIp);
        }
        if (!StringUtils.isBlank(osgiRemoteShellListenPort)) {
            // find free port
            String[] tokens = osgiRemoteShellListenPort.split("[\\s,]+");
            int[] ports = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                ports[i] = Integer.parseInt(tokens[i]);
            }
            Integer port = NetUtils.getFreePort(ports);
            if (port != null) {
                OSGI_BOOTSTRAP.setRemoteShellListenPort(port);
            }
        }
        if (!StringUtils.isBlank(osgiContainerLocation)) {
            OSGI_BOOTSTRAP.setOsgiContainerLocation(osgiContainerLocation);
        }
        OSGI_BOOTSTRAP.setServletContext(servletContext);

        try {
            OSGI_BOOTSTRAP.init();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("OSGI_BOOTSTRAP", OSGI_BOOTSTRAP);
        new _DaspGlobal(data);
    }

    private void detroyTempDir() {
        try {
            CTX_TMP_DIR.delete();
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage(), t);
        }
    }

    private void initTempDir(ServletContext servletContext) {
        String systemTempDir = System.getProperty("java.io.tmpdir");
        String randomStr = "CTX_" + RandomStringUtils.randomAlphanumeric(16);
        CTX_TMP_DIR = new TempDir(systemTempDir, randomStr);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("CONTEXT_TEMPDIR", CTX_TMP_DIR);
        new _DaspGlobal(data);
    }

    private class _DaspGlobal extends DaspGlobal {
        public _DaspGlobal(Map<String, Object> data) {
            Object obj;

            obj = data.get("SERVLET_CONTEXT");
            if (obj instanceof ServletContext) {
                setServletContext((ServletContext) obj);
            }

            obj = data.get("CONTEXT_TEMPDIR");
            if (obj instanceof TempDir) {
                setContextTempDir((TempDir) obj);
            }

            obj = data.get("OSGI_BOOTSTRAP");
            if (obj instanceof IOsgiBootstrap) {
                setOsgiBootstrap((IOsgiBootstrap) obj);
            }
        }
    }
}
