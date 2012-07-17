package ddth.dasp.common.logging;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcConnLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(JdbcConnLogger.class);

    private static ThreadLocal<Set<Connection>> threadOpenedConnections = new ThreadLocal<Set<Connection>>() {
        /**
         * {@inheritDoc}
         */
        @Override
        protected Set<Connection> initialValue() {
            return new HashSet<Connection>();
        }
    };

    public static void add(Connection conn) {
        threadOpenedConnections.get().add(conn);
    }

    public static void remove(Connection conn) {
        threadOpenedConnections.get().remove(conn);
    }

    public static void cleanUp() {
        try {
            Set<Connection> openConnections = threadOpenedConnections.get();
            if (openConnections.size() > 0) {
                LOGGER.warn("Bad, [" + openConnections.size() + "] leak JDBC connection(s)!");
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
        } finally {
            threadOpenedConnections.remove();
        }
    }
}
