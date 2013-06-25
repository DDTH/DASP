package ddth.dasp.common.logging;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.RequestLocal;

public class JdbcConnLogger {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(JdbcConnLogger.class);

	private final static String REQUEST_LOCAL_KEY = "JDBC_CONN_LOG";

	@SuppressWarnings("unchecked")
	private static Set<Connection> getLogs(RequestLocal requestLocal,
			boolean createIfNotExist) {
		if (requestLocal != null) {
			Set<Connection> logs = requestLocal.getLocalVariable(
					REQUEST_LOCAL_KEY, Set.class);
			if (logs == null && createIfNotExist) {
				logs = new HashSet<Connection>();
				requestLocal.setLocalVariable(REQUEST_LOCAL_KEY, logs);
			}
			return logs;
		}
		return null;
	}

	public static void add(Connection conn) {
		add(conn, RequestLocal.get());
	}

	public static void add(Connection conn, RequestLocal requestLocal) {
		if (conn != null && requestLocal != null) {
			Set<Connection> openConnections = getLogs(requestLocal, true);
			openConnections.add(conn);
		}
	}

	public static void remove(Connection conn) {
		remove(conn, RequestLocal.get());
	}

	public static void remove(Connection conn, RequestLocal requestLocal) {
		if (conn != null && requestLocal != null) {
			Set<Connection> openConnections = getLogs(requestLocal, true);
			openConnections.remove(conn);
		}
	}

	public static void cleanUp() {
		cleanUp(RequestLocal.get());
	}

	public static void cleanUp(RequestLocal requestLocal) {
		if (requestLocal != null) {
			Set<Connection> openConnections = getLogs(requestLocal, true);
			if (openConnections.size() > 0) {
				LOGGER.error("Bad, [" + openConnections.size()
						+ "] leak JDBC connection(s)!");
				for (Connection conn : openConnections) {
					try {
						conn.close();
					} catch (Exception e) {
						LOGGER.warn(e.getMessage(), e);
					}
				}
			} else {
				LOGGER.debug("Good, No leaked JDBC connection");
			}
		}
	}
}
