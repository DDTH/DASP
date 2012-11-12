package ddth.dasp.framework.bo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Delegate-enabled Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class DelegateBoManager extends CacheBoManager {

	private BaseBoManager delegatedBoManager;

	protected BaseBoManager getDelegatedBoManager() {
		return delegatedBoManager;
	}

	public void setDelegatedBoManager(BaseBoManager delegatedBoManager) {
		this.delegatedBoManager = delegatedBoManager;
	}

	protected Object delegate(String methodName, Object[] params,
			Class<?>[] paramTypes) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (delegatedBoManager == null) {
			return null;
		}
		Method m = delegatedBoManager.getClass().getMethod(methodName,
				paramTypes);
		Object result = m.invoke(delegatedBoManager, params);
		return result;
	}
}
