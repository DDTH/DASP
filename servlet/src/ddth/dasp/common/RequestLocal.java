package ddth.dasp.common;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.common.id.IdGenerator;

/**
 * Mimic the concept of {@link ThreadLocal}, but for a "request".
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class RequestLocal {

	private static ThreadLocal<RequestLocal> threadRequestlocal = new ThreadLocal<RequestLocal>();

	/**
	 * Gets {@link RequestLocal} instance from the thread local if any.
	 * 
	 * @return
	 */
	public static RequestLocal get() {
		return threadRequestlocal.get();
	}

	/**
	 * Puts a {@link RequestLocal} instance to the thread local.
	 * 
	 * @param requestLocal
	 */
	public static void set(RequestLocal requestLocal) {
		threadRequestlocal.set(requestLocal);
	}

	/**
	 * Removes the {@link RequestLocal} instance from the thread local.
	 */
	public static void remove() {
		threadRequestlocal.remove();
	}

	private String id = IdGenerator.getInstance(IdGenerator.getMacAddr())
			.generateId64Hex();
	private Map<String, Object> localVariables = new HashMap<String, Object>();

	public void init() {
		// EMPTY
	}

	public void destroy() {
		localVariables.clear();
	}

	@Override
	protected void finalize() {
		localVariables.clear();
	}

	public String getId() {
		return id;
	}

	/**
	 * Gets a request local variable.
	 * 
	 * @param name
	 * @return
	 */
	public Object getLocalVariable(String name) {
		return localVariables.get(name);
	}

	/**
	 * Gets a request local variable.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getLocalVariable(String name, Class<T> clazz) {
		Object result = getLocalVariable(name);
		if (result != null && clazz.isAssignableFrom(result.getClass())) {
			return (T) result;
		}
		return null;
	}

	/**
	 * Sets a local variable.
	 * 
	 * @param name
	 * @param value
	 */
	public void setLocalVariable(String name, Object value) {
		localVariables.put(name, value);
	}
}
