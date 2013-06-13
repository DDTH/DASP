package ddth.dasp.common.logging;

/**
 * This interface declares API to write log messages.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface ILogWriter {
    /**
     * Writes a log message to persistent storage.
     * 
     * @param msg
     */
    public void writeLog(String msg);
}
