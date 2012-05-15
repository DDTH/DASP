package ddth.dasp.servlet.logging;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class represents a request log entry.
 * 
 * Log information:
 * <ul>
 * <li>Log's id: unique of the log entry.
 * <li>Session's id: id of the session associated with the log entry.
 * <li>Node's id: id of the node (app server) where the log entry originates.
 * <li>Client's id: id of the client where the request originates.
 * <li>Client's ip: ip address of the client where the request originates.
 * <li>Request timestamp: timestamp when the request occurs.
 * <li>Serve time: Total time taken to serve this request ( >= parsing time +
 * handling time)
 * <li>Request Parsing time: Time taken to parse the request.
 * <li>Request Handling time: Time taken to handle the request.
 * <li>Request uri: the requested uri.
 * <li>Input: raw request input.
 * <li>InputStr: request input as string (UTF-8)
 * <li>ResponseCode: request's response code
 * <li>Output: raw request output.
 * <li>OutputStr: request output as string (UTF-8)
 * </ul>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class RequestLogEntry implements Serializable, Cloneable {

    private static final long serialVersionUID = 1;

    /**
     * Unique of the log entry.
     */
    private String id = UUID.randomUUID().toString().toLowerCase();

    /**
     * Id of the session associated with the log entry.
     */
    private String sessionId;

    /**
     * ID of the node (app server) where the log entry originates.
     */
    private String nodeId;

    /**
     * ID of the client where the request originates.
     */
    private String clientId;

    /**
     * IP address of the client where the request originates.
     */
    private String clientIp;

    /**
     * Timestamp when the log occurs
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * Total time taken to serve this request
     */
    private long serveTime;

    /**
     * Time taken to parse this request
     */
    private long parseTime;

    /**
     * Time taken to handle this request
     */
    private long handleTime;

    /**
     * The requested uri
     */
    private String requestUri;

    /**
     * The raw input
     */
    private byte[] input;

    /**
     * The input as string (UTF-8)
     */
    private String inputString;

    /**
     * The request's response code
     */
    private String responseCode;

    /**
     * The raw response
     */
    private byte[] output;

    /**
     * The output as string (UTF-8)
     */
    private String outputString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public long getServeTime() {
        return serveTime;
    }

    public void setServeTime(long serveTime) {
        this.serveTime = serveTime;
    }

    public long getParseTime() {
        return parseTime;
    }

    public void setParseTime(long parseTime) {
        this.parseTime = parseTime;
    }

    public long getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(long handleTime) {
        this.handleTime = handleTime;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public byte[] getInput() {
        return input;
    }

    public void setInput(byte[] input) {
        this.input = input;
        try {
            this.inputString = input != null ? new String(input, "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            // impossible to happen!
        }
    }

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public byte[] getOutput() {
        return output;
    }

    public void setOutput(byte[] output) {
        this.output = output;
        try {
            this.outputString = output != null ? new String(output, "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            // impossible to happen!
        }
    }

    public String getOutputString() {
        return outputString;
    }

    public void setOutputString(String outputString) {
        this.outputString = outputString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestLogEntry clone() throws CloneNotSupportedException {
        RequestLogEntry result = (RequestLogEntry) super.clone();
        result.clientId = this.clientId;
        result.id = this.id;
        result.input = this.input;
        result.inputString = this.inputString;
        result.nodeId = this.nodeId;
        result.output = this.output;
        result.outputString = this.outputString;
        result.requestUri = this.requestUri;
        result.responseCode = this.responseCode;
        result.serveTime = this.serveTime;
        result.sessionId = this.sessionId;
        result.timestamp = this.timestamp;
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
        if (obj == null || !(obj instanceof RequestLogEntry)) {
            return false;
        }
        RequestLogEntry other = (RequestLogEntry) obj;
        return new EqualsBuilder().append(input, other.input).append(output, other.output).append(
                clientId, other.clientId).append(id, other.id).append(inputString,
                other.inputString).append(nodeId, other.nodeId).append(outputString,
                other.outputString).append(requestUri, other.requestUri).append(responseCode,
                other.responseCode).append(serveTime, other.serveTime).append(sessionId,
                other.sessionId).append(timestamp, other.timestamp).isEquals();
    }
}
