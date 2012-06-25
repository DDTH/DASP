package ddth.dasp.framework.logging.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ddth.dasp.framework.logging.AppLogEntry;
import ddth.dasp.framework.logging.IAppLogEngine;
import ddth.dasp.framework.logging.ILogWriter;
import ddth.dasp.framework.utils.JsonUtils;

/**
 * This implementation of {@link IAppLogEngine} stores log via an instance of
 * {@link ILogWriter} .
 * 
 * @author ThanhNB
 */
public class LogWriterAppLogEngine implements IAppLogEngine {

	public final static String FIELD_ID = "id";
	public final static String FIELD_ACTION = "action";
	public final static String FIELD_CLIENT_IP = "clientIp";
	public final static String FIELD_MESSAGE = "message";
	public final static String FIELD_NODE_ID = "nodeId";
	public final static String FIELD_REQUEST_ID = "requestId";
	public final static String FIELD_TAGS = "tags";
	public final static String FIELD_TIMESTAMP = "timestamp";
	public final static String FIELD_TYPE = "type";
	public final static String FIELD_DATETIME = "datetime";

	private ILogWriter logWriter;

	/**
	 * Getter for {@link #logWriter}
	 * 
	 * @return ILogWriter
	 */
	public ILogWriter getLogWriter() {
		return logWriter;
	}

	/**
	 * Setter for {@link #logWriter}
	 * 
	 * @param logWriter
	 *            ILogWriter
	 */
	public void setLogWriter(ILogWriter logWriter) {
		this.logWriter = logWriter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void log(AppLogEntry entry) {
		Map<String, Object> log = new HashMap<String, Object>();
		log.put(FIELD_ID, entry.getId());
		log.put(FIELD_ACTION, entry.getAction());
		log.put(FIELD_CLIENT_IP, entry.getClientIp());
		log.put(FIELD_MESSAGE, entry.getMessage());
		log.put(FIELD_NODE_ID, entry.getNodeId());
		log.put(FIELD_REQUEST_ID, entry.getRequestId());
		log.put(FIELD_TAGS, entry.getTags());
		log.put(FIELD_TIMESTAMP, entry.getTimestamp());
		log.put(FIELD_TYPE, entry.getType());
		log.put(FIELD_DATETIME, new Date().toString());
		logWriter.writeLog(JsonUtils.toJson(log));
	}
}
