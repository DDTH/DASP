package ddth.dasp.framework.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.regex.Pattern;

/**
 * Model object: to be used on view.
 * 
 * Use this class as starting point for model objects.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class BaseModel<T> implements InvocationHandler {
    private final static Pattern PATTERN_METHOD_GET = Pattern.compile("^get[A-Z]\\w*$");
    private T obj;

    @SuppressWarnings("unchecked")
    public static <T> T createModel(Class<T> interf, final T obj) {
        BaseModel<T> model = new BaseModel<T>(obj);
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] { interf },
                model);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createModel(Class<T> interf,
            final Class<? extends BaseModel<T>> modelClazz, final T obj) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        Constructor<BaseModel<T>> c = (Constructor<BaseModel<T>>) modelClazz.getConstructor(interf);
        BaseModel<T> model = c.newInstance(obj);
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] { interf },
                model);
    }

    protected BaseModel(T obj) {
        setTargetObject(obj);
    }

    protected void setTargetObject(T obj) {
        this.obj = obj;
    }

    protected T getTargetObject() {
        return obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (!PATTERN_METHOD_GET.matcher(methodName).matches()) {
            throw new IllegalAccessException("Invoking method [" + methodName + "] is not allowed!");

        }
        Method m = null;
        try {
            m = this.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
        }
        if (m != null) {
            return m.invoke(this);
        }
        return method.invoke(getTargetObject(), args);
    }
}
