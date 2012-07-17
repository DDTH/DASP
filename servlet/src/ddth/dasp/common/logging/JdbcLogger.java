package ddth.dasp.common.logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Application JDBC logger.
 * 
 * @author ThanhNB
 */
public class JdbcLogger {

    protected static ThreadLocal<List<JdbcLogEntry>> logs = new ThreadLocal<List<JdbcLogEntry>>() {
        /**
         * {@inheritDoc}
         */
        @Override
        protected List<JdbcLogEntry> initialValue() {
            return new ArrayList<JdbcLogEntry>();
        }
    };

    /**
     * Removes the currently bound JDBC log entry list
     */
    public static void remove() {
        logs.remove();
    }

    /**
     * Logs a JDBC statement
     * 
     * @param logEntry
     */
    public static void log(JdbcLogEntry logEntry) {
        if (logEntry != null) {
            List<JdbcLogEntry> logList = logs.get();
            logList.add(logEntry);
        }
    }

    /**
     * Gets the current JDBC log entry list.
     * 
     * @return
     */
    public static JdbcLogEntry[] get() {
        List<JdbcLogEntry> logList = logs.get();
        return logList.toArray(new JdbcLogEntry[0]);
    }
}
