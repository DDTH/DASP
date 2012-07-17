package ddth.dasp.common.logging;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ddth.dasp.common.id.IdGenerator;

/**
 * This class represents a JDBC log entry.
 * 
 * Log information:
 * <ul>
 * <li>Log's id: unique of the log entry.
 * <li>Request id: id of the associated request.
 * <li>Node's id: id of the node (app server) where the log entry originates.
 * <li>Client's id: id of the client where the request originates.
 * <li>Enduser's id: id of the enduser.
 * <li>Timestamp: timestamp when the log occurs.
 * <li>EndTimestamp: timestamp when the log occurs.
 * <li>Sql: the SQL statement
 * </ul>
 * 
 * @author ThanhNB
 */
public class JdbcLogEntry implements Serializable, Cloneable {

    private static final long serialVersionUID = "$Revision$".hashCode();
    private static final IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

    /**
     * Unique of the log entry.
     */
    private long id = idGen.generateId64();

    /**
     * ID of the associated request.
     */
    private String requestId;

    /**
     * ID of the node (app server) where the log entry originates.
     */
    private String nodeId;

    /**
     * ID of the client where the request originates. Usually it's the client's
     * IP address.
     */
    private String clientId;

    /**
     * ID of the enduser where the request originates. Usually it's the
     * enduser's IP address.
     */
    private String enduserId;

    /**
     * Timestamp when the log occurs
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * The SQL statement.
     */
    private String sql;

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
    public JdbcLogEntry(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEnduserId() {
        return enduserId;
    }

    public void setEnduserId(String enduserId) {
        this.enduserId = enduserId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcLogEntry clone() throws CloneNotSupportedException {
        synchronized (this) {
            JdbcLogEntry result = (JdbcLogEntry) super.clone();
            result.clientId = this.clientId;
            result.enduserId = this.enduserId;
            result.id = this.id;
            result.nodeId = this.nodeId;
            result.requestId = this.requestId;
            result.sql = this.sql;
            result.timestamp = this.timestamp;

            return result;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 81).append(id).append(nodeId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof JdbcLogEntry)) {
            return false;
        }
        JdbcLogEntry other = (JdbcLogEntry) obj;
        return new EqualsBuilder().append(id, other.id).append(requestId, other.requestId)
                .append(nodeId, other.nodeId).append(clientId, other.clientId)
                .append(enduserId, other.enduserId).append(sql, other.sql)
                .append(timestamp, other.timestamp).isEquals();
    }
}
