package ddth.dasp.common.logging;

import java.util.ArrayList;
import java.util.List;

import ddth.dasp.common.RequestLocal;

/**
 * Application JDBC logger.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JdbcLogger {

	private final static String REQUEST_LOCAL_KEY = "JDBC_LOG";
	private final static JdbcLogEntry[] EMPTY_ARRAY = new JdbcLogEntry[0];

	@SuppressWarnings("unchecked")
	private static List<JdbcLogEntry> getLogs(RequestLocal requestLocal,
			boolean createIfNotExist) {
		if (requestLocal != null) {
			List<JdbcLogEntry> logs = requestLocal.getLocalVariable(
					REQUEST_LOCAL_KEY, List.class);
			if (logs == null && createIfNotExist) {
				logs = new ArrayList<JdbcLogEntry>();
				requestLocal.setLocalVariable(REQUEST_LOCAL_KEY, logs);
			}
			return logs;
		}
		return null;
	}

	public static void log(JdbcLogEntry logEntry) {
		log(logEntry, RequestLocal.get());
	}

	/**
	 * Logs a JDBC statement
	 * 
	 * @param logEntry
	 */
	public static void log(JdbcLogEntry logEntry, RequestLocal requestLocal) {
		if (logEntry != null && requestLocal != null) {
			List<JdbcLogEntry> logs = getLogs(requestLocal, true);
			logs.add(logEntry);
		}
	}

	public static JdbcLogEntry[] get() {
		return get(RequestLocal.get());
	}

	/**
	 * Gets the current JDBC log entry list.
	 * 
	 * @return
	 */
	public static JdbcLogEntry[] get(RequestLocal requestLocal) {
		if (requestLocal != null) {
			List<JdbcLogEntry> logs = getLogs(requestLocal, false);
			return logs != null ? logs.toArray(EMPTY_ARRAY) : EMPTY_ARRAY;
		}
		return null;
	}
}
