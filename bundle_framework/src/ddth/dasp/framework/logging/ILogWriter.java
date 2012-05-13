package ddth.dasp.framework.logging;

/**
 * This interface declares API to write log messages.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface ILogWriter {
    /**
     * Writes a log message to persistent storage.
     * 
     * @param logMsg
     *            String
     */
    public void writeLog(String logMsg);
}
