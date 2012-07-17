package ddth.dasp.common.logging;

import java.io.Serializable;

/**
 * This class represents a JDBC log entry.
 * 
 * Log information:
 * <ul>
 * <li>Start Timestamp: timestamp when the SQL statement starts its execution
 * <li>End Timestamp: timestamp when the SQL statement ends its execution
 * <li>Sql: the SQL statement
 * <li>Params: parameters feed to the SQL statement
 * </ul>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JdbcLogEntry implements Serializable {

    private static final long serialVersionUID = "$Revision$".hashCode();

    /**
     * Timestamp when the SQL statement starts its execution.
     */
    private long startTimestamp;

    /**
     * Timestamp when the SQL statement ends its execution.
     */
    private long endTimestamp;

    /**
     * The SQL statement.
     */
    private String sql;

    /**
     * The SQL statement's parameters.
     */
    private Object params;

    /**
     * Constructs a new {@link JdbcLogEntry} instance.
     */
    public JdbcLogEntry() {
    }

    /**
     * Constructs a new {@link JdbcLogEntry} instance and specifies an ID.
     * 
     * @param id
     */
    public JdbcLogEntry(long startTimestamp, long endTimestamp, String sql, Object params) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.sql = sql;
        this.params = params;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getDuration() {
        return endTimestamp - startTimestamp;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

}
