package ddth.dasp.common;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;

import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.tempdir.TempDir;

public class DaspGlobal {
    private static IOsgiBootstrap osgiBootstrap;
    private static TempDir contextTempDir;
    private static ServletContext servletContext;

    private static Timer timer = new Timer(DaspGlobal.class.getName(), true);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private static ConcurrentMap<String, Object> globalStorage = new ConcurrentHashMap<String, Object>();

    public static Object getGlobalVar(String name) {
        return globalStorage.get(name);
    }

    public static void removeGlobalVar(String name) {
        globalStorage.remove(name);
    }

    public static void setGlobalVar(String name, Object value) {
        if (value == null) {
            removeGlobalVar(name);
        } else {
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

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static Timer getTimer() {
        return timer;
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
