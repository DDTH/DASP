package ddth.dasp.common.logging;

import ddth.dasp.common.RequestLocal;

/**
 * Application profiling logger.
 * 
 * Usage:
 * 
 * <pre>
 * ProfileLogger.push(&quot;marker_name_1&quot;);
 * // do some work
 * ProfileLogger.pop();
 * 
 * ProfileLogger.push(&quot;marker_name_2&quot;);
 * // do some other work
 * ProfileLogger.push(&quot;marker_name_child_1&quot;);
 * // do some child work
 * ProfileLogger.pop();
 * ProfileLogger.pop();
 * 
 * ProfileLogEntry logEntry = ProfileLogger.get();
 * // do something with logEntry
 * </pre>
 * 
 * @author ThanhNB
 */
public class ProfileLogger {

	private final static String REQUEST_LOCAL_KEY = "PROFILE_LOG";

	private static ProfileLogEntry getLogs(RequestLocal requestLocal,
			boolean createIfNotExist) {
		if (requestLocal != null) {
			ProfileLogEntry log = requestLocal.getLocalVariable(
					REQUEST_LOCAL_KEY, ProfileLogEntry.class);
			if (log == null && createIfNotExist) {
				log = new ProfileLogEntry();
				requestLocal.setLocalVariable(REQUEST_LOCAL_KEY, log);
			}
			return log;
		}
		return null;
	}

	public static ProfileLogEntry push(String name) {
		return push(name, RequestLocal.get());
	}

	/**
	 * Pushes a profiling to the stack.
	 * 
	 * @param name
	 *            String
	 * @return ProfileLogEntry the currently bound {@link ProfileLogEntry}
	 *         object.
	 */
	public static ProfileLogEntry push(String name, RequestLocal requestLocal) {
		if (name != null && requestLocal != null) {
			ProfileLogEntry log = getLogs(requestLocal, true);
			log.push(name);
			return log;
		}
		return null;
	}

	public static ProfileLogEntry pop() {
		return pop(RequestLocal.get());
	}

	/**
	 * Pops the last profiling from the stack.
	 * 
	 * @return ProfileLogEntry
	 */
	public static ProfileLogEntry pop(RequestLocal requestLocal) {
		ProfileLogEntry log = getLogs(requestLocal, false);
		if (log != null) {
			log.pop();
		}
		return log;
	}

	public static ProfileLogEntry get() {
		return get(RequestLocal.get());
	}

	public static ProfileLogEntry get(RequestLocal requestLocal) {
		return getLogs(requestLocal, false);
	}
}
