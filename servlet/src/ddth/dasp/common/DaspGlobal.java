package ddth.dasp.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;

import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.tempdir.TempDir;

public class DaspGlobal {

    private static IOsgiBootstrap osgiBootstrap;
    private static TempDir contextTempDir;
    private static Timer contextTimer = new Timer(DaspGlobal.class.getName(), true);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private static ServletContext servletContext;
    private static Map<String, Object> globalStorage = new HashMap<String, Object>();

    public static Object getGlobalVar(String name) {
        synchronized (globalStorage) {
            return globalStorage.get(name);
        }
    }

    public static void setGlobalVar(String name, Object value) {
        synchronized (globalStorage) {
            globalStorage.put(name, value);
        }
    }

    protected void setOsgiBootstrap(IOsgiBootstrap osgiBootstrap) {
        if (DaspGlobal.osgiBootstrap == null) {
            DaspGlobal.osgiBootstrap = osgiBootstrap;
        }
    }

    protected void setContextTempDir(TempDir contextTempDir) {
        if (DaspGlobal.contextTempDir == null) {
            DaspGlobal.contextTempDir = contextTempDir;
        }
    }

    protected void setServletContext(ServletContext servletContext) {
        if (DaspGlobal.servletContext == null) {
            DaspGlobal.servletContext = servletContext;
        }
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static Timer getContextTimer() {
        return contextTimer;
    }

    /**
     * Gets the initialized instance of {@link IOsgiBootstrap}.
     * 
     * @return
     */
    public static IOsgiBootstrap getOsgiBootstrap() {
        return osgiBootstrap;
    }

    /**
     * Gets the context's {@link TempDir} instance.
     * 
     * @return
     */
    public static TempDir getContextTempDir() {
        return contextTempDir;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }
}
