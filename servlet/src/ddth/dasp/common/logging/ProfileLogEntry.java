package ddth.dasp.common.logging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ddth.dasp.common.id.IdGenerator;

/**
 * This class represents a profiling log entry.
 * 
 * Log information:
 * <ul>
 * <li>Log's id: unique of the log entry.
 * <li>Request id: id of the associated request.
 * <li>Node's id: id of the node (app server) where the log entry originates.
 * <li>Client's id: id of the client where the request originates.
 * <li>Enduser's id: id of the enduser.
 * <li>Timestamp: timestamp when the log occurs.
 * </ul>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ProfileLogEntry implements Serializable, Cloneable {

	private static final long serialVersionUID = "$Revision$".hashCode();
	private static final IdGenerator idGen = IdGenerator
			.getInstance(IdGenerator.getMacAddr());

	public static final String KEY_NAME = "NAME";
	private static final String KEY_START_TIMESTAMP = "START_TIMESTAMP";
	private static final String KEY_END_TIMESTAMP = "END_TIMESTAMP";
	public static final String KEY_EXECUTION_TIME = "EXECUTION_TIME";
	public static final String KEY_EXECUTION_TIME_MILLIS = "EXECUTION_TIME_MILLIS";
	public static final String KEY_CHILDREN = "CHILDREN";
	private static final String KEY_PARENT = "PARENT";

	private List<Map<String, Object>> profilingData = new LinkedList<Map<String, Object>>();
	private Object root = profilingData;
	private Object current = null;
	// private Object parent = null;

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
	 * Constructs a new {@link ProfileLogEntry} instance.
	 */
	public ProfileLogEntry() {
	}

	/**
	 * Constructs a new {@link ProfileLogEntry} instance and specifies an ID.
	 * 
	 * @param id
	 */
	public ProfileLogEntry(long id) {
		this.id = id;
	}

	/**
	 * Pushes a profiling data to the tree.
	 * 
	 * @param name
	 *            String name
	 */
	public void push(String name) {
		synchronized (root) {
			Map<String, Object> entry = createEntry(name, current);
			// parent = current;
			if (current != null) {
				addChildren(current, entry);
			} else {
				profilingData.add(entry);
			}
			current = entry;
		}
	}

	@SuppressWarnings("unchecked")
	private static void addChildren(Object current, Object child) {
		Map<String, Object> map = (Map<String, Object>) current;
		Object children = map.get(KEY_CHILDREN);
		if (!(children instanceof List<?>)) {
			children = new LinkedList<Object>();
			map.put(KEY_CHILDREN, children);
		}
		((List<Object>) children).add(child);
	}

	private static Map<String, Object> createEntry(String name, Object parent) {
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put(KEY_CHILDREN, new LinkedList<Map<String, Object>>());
		entry.put(KEY_NAME, name);
		entry.put(KEY_PARENT, parent);
		entry.put(KEY_START_TIMESTAMP, System.nanoTime());
		return entry;
	}

	/**
	 * Pops the last profiling data from the tree.
	 */
	public void pop() {
		if (current == null) {
			throw new IllegalStateException();
		}
		synchronized (root) {
			setField(current, KEY_END_TIMESTAMP, System.nanoTime());
			current = getField(current, KEY_PARENT);
			// current = getParent(current);
			// parent = getParent(this.current);
		}
	}

	// @SuppressWarnings("unchecked")
	// private static Object getParent(Object current) {
	// if (current instanceof Map<?, ?>) {
	// Map<String, Object> map = (Map<String, Object>) current;
	// return map.get(KEY_PARENT);
	// }
	// return null;
	// }

	// @SuppressWarnings("unchecked")
	// private static void updateEndTimestamp(Object current) {
	// if (current instanceof Map<?, ?>) {
	// Map<String, Object> map = (Map<String, Object>) current;
	// map.put(KEY_END_TIMESTAMP, System.nanoTime());
	// }
	// }

	@SuppressWarnings("unchecked")
	private static Object getField(Object current, String fieldName) {
		if (current instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) current;
			return map.get(fieldName);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static void setField(Object current, String fieldName, Object value) {
		if (current instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) current;
			map.put(fieldName, value);
		}
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

	public Object[] getProfiling() {
		List<Object> result = new LinkedList<Object>();
		synchronized (root) {
			Object current = this.current;
			while (current != null) {
				Object endTimestamp = getField(current, KEY_END_TIMESTAMP);
				if (endTimestamp == null || !(endTimestamp instanceof Number)) {
					endTimestamp = System.nanoTime();
					setField(current, KEY_END_TIMESTAMP, endTimestamp);
				}
				current = getField(current, KEY_PARENT);
				// current = getParent(current);
			}
			for (Object entry : (List<?>) root) {
				result.add(buildProfilingNode(entry));
			}
		}
		return result.toArray();
	}

	static Object buildProfilingNode(Object entry) {
		if (entry == null) {
			return null;
		}

		Map<String, Object> result = new HashMap<String, Object>();

		Object name = getField(entry, KEY_NAME);
		if (name == null) {
			name = "";
		}
		result.put(KEY_NAME, name.toString());

		Object startTimestamp = getField(entry, KEY_START_TIMESTAMP);
		if (startTimestamp == null || !(startTimestamp instanceof Number)) {
			startTimestamp = 0;
		}
		Object endTimestamp = getField(entry, KEY_END_TIMESTAMP);
		if (endTimestamp == null || !(endTimestamp instanceof Number)) {
			endTimestamp = 0;
		}
		long executionTime = ((Number) endTimestamp).longValue()
				- ((Number) startTimestamp).longValue();
		result.put(KEY_EXECUTION_TIME, executionTime);
		result.put(KEY_EXECUTION_TIME_MILLIS, executionTime / 1e6);

		List<Object> children = new LinkedList<Object>();
		Object _children = getField(entry, KEY_CHILDREN);
		if (_children == null || !(_children instanceof List<?>)) {
			_children = new LinkedList<Object>();
		}
		for (Object _child : (List<?>) _children) {
			children.add(buildProfilingNode(_child));
		}
		result.put(KEY_CHILDREN, children.toArray());

		return result;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileLogEntry clone() throws CloneNotSupportedException {
		synchronized (this) {
			ProfileLogEntry result = (ProfileLogEntry) super.clone();
			result.clientId = this.clientId;
			result.enduserId = this.enduserId;
			result.id = this.id;
			result.nodeId = this.nodeId;
			result.requestId = this.requestId;
			result.timestamp = this.timestamp;

			// FIXME
			result.profilingData = this.profilingData;
			result.current = this.current;
			result.root = this.root;

			return result;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 81).append(id).append(nodeId)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof ProfileLogEntry)) {
			return false;
		}
		ProfileLogEntry other = (ProfileLogEntry) obj;
		return new EqualsBuilder().append(id, other.id)
				.append(requestId, other.requestId)
				.append(nodeId, other.nodeId).append(clientId, other.clientId)
				.append(enduserId, other.enduserId)
				.append(timestamp, other.timestamp).isEquals();
	}
}
