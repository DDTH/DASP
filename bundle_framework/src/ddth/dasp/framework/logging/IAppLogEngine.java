package ddth.dasp.framework.logging;

/**
 * Application log engine APIs.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IAppLogEngine {

    public final static String LOG_TYPE_ERROR = "ERROR";
    public final static String LOG_TYPE_WARNING = "WARNING";
    public final static String LOG_TYPE_INFO = "INFO";
    public final static String LOG_TYPE_DEBUG = "DEBUG";

    /**
     * Logs a entry.
     * 
     * @param entry
     *            AppLogEntry
     */
    public void log(AppLogEntry entry);
}
