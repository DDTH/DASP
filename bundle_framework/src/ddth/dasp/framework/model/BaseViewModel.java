package ddth.dasp.framework.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ddth.dasp.framework.url.IUrlCreator;

/**
 * View Model object: to be used on view. Use case: A view model object wraps a
 * business object and delegate only "get" method calls to the underlying
 * object.
 * 
 * Use this class as starting point for model objects.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class BaseViewModel<T> implements InvocationHandler {
    private final static Pattern PATTERN_METHOD_GET = Pattern.compile("^get[A-Z]\\w*$");
    private T obj;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private IUrlCreator urlCreator;

    private static <T> void initModelObj(BaseViewModel<T> model, HttpServletRequest request,
            HttpServletResponse response, IUrlCreator urlCreator) {
        model.setHttpRequest(request);
        model.setHttpResponse(response);
        model.setUrlCreator(urlCreator);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, T obj) {
        BaseViewModel<T> model = new BaseViewModel<T>(obj);
        initModelObj(model, request, response, urlCreator);
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass()
                .getInterfaces(), model);
    }

    public static <T> T[] createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, T[] objs) {
        List<T> result = new ArrayList<T>();
        for (T obj : objs) {
            T t = createModel(request, response, urlCreator, obj);
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray(objs);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, Class<? extends BaseViewModel<T>> modelClazz,
            Class<?> targetClass, T obj) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        Class<?>[] interfacesModel = modelClazz.getInterfaces();
        for (Class<?> interf : interfacesModel) {
            interfaces.add(interf);
        }
        Class<?>[] interfacesTarget = obj.getClass().getInterfaces();
        for (Class<?> interf : interfacesTarget) {
            interfaces.add(interf);
        }

        Constructor<BaseViewModel<T>> c = (Constructor<BaseViewModel<T>>) modelClazz
                .getDeclaredConstructor(targetClass);
        c.setAccessible(true);
        BaseViewModel<T> model = c.newInstance(obj);
        initModelObj(model, request, response, urlCreator);
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                interfaces.toArray(new Class[0]), model);
    }

    public static <T> T[] createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, Class<? extends BaseViewModel<T>> modelClazz,
            Class<?> targetClass, T[] objs) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        List<T> result = new ArrayList<T>();
        for (T obj : objs) {
            T t = createModel(request, response, urlCreator, modelClazz, targetClass, obj);
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray(objs);
    }

    protected BaseViewModel(T obj) {
        setTargetObject(obj);
    }

    protected void setTargetObject(T obj) {
        this.obj = obj;
    }

    protected T getTargetObject() {
        return obj;
    }

    protected void setHttpRequest(HttpServletRequest request) {
        this.request = request;
    }

    protected HttpServletRequest getHttpRequest() {
        return request;
    }

    protected void setHttpResponse(HttpServletResponse response) {
        this.response = response;
    }

    protected HttpServletResponse getHttpResponse() {
        return response;
    }

    protected void setUrlCreator(IUrlCreator urlCreator) {
        this.urlCreator = urlCreator;
    }

    protected IUrlCreator getUrlCreator() {
        return urlCreator;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (!"toString".equals(methodName) && "hashCode".equals(methodName)
                & !PATTERN_METHOD_GET.matcher(methodName).matches()) {
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
