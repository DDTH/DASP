package ddth.dasp.framework.logging;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class represents an application log entry.
 * 
 * Log information:
 * <ul>
 * <li>Log's id: unique of the log entry.
 * <li>Request id: id of the associated request.
 * <li>Node's id: id of the node (app server) where the log entry originates.
 * <li>Client's ip: ip address of the client where the request originates.
 * <li>Timestamp: timestamp when the log occurs.
 * <li>Log type: type/category of the log.
 * <li>Log action: the action to be logged.
 * <li>Log message: the message to be logged.
 * <li>Log tags: for misc use.
 * </ul>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class AppLogEntry implements Serializable, Cloneable {

    private static final long serialVersionUID = 1;

    /**
     * Unique of the log entry.
     */
    private String id = UUID.randomUUID().toString().toLowerCase();

    /**
     * ID of the associated request.
     */
    private String requestId;

    /**
     * ID of the node (app server) where the log entry originates.
     */
    private String nodeId;

    /**
     * IP address of the client where the request originates.
     */
    private String clientIp;

    /**
     * Timestamp when the log occurs
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * Type/category of the log.
     */
    private String type;

    /**
     * The action to be logged.
     */
    private String action;

    /**
     * The message to be logged.
     */
    private String message;

    /**
     * Tags for misc usage.
     */
    private String tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public AppLogEntry() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppLogEntry clone() throws CloneNotSupportedException {
        AppLogEntry result = (AppLogEntry) super.clone();
        result.action = this.action;
        result.clientIp = this.clientIp;
        result.id = this.id;
        result.message = this.message;
        result.nodeId = this.nodeId;
        result.requestId = this.requestId;
        result.tags = this.tags;
        result.timestamp = this.timestamp;
        result.type = this.type;
        return result;
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
        if (obj == null || !(obj instanceof AppLogEntry)) {
            return false;
        }
        AppLogEntry other = (AppLogEntry) obj;
        return new EqualsBuilder().append(action, other.action).append(clientIp, other.clientIp)
                .append(id, other.id).append(message, other.message).append(nodeId, other.nodeId)
                .append(requestId, requestId).append(timestamp, other.timestamp).append(tags,
                        other.tags).append(this.type, other.type).isEquals();
    }
}
