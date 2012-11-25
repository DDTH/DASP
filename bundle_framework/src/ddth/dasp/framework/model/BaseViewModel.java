package ddth.dasp.framework.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private List<Object> optionalAttrs = new LinkedList<Object>();

    protected static <T> BaseViewModel<T> initModelObj(BaseViewModel<T> model,
            HttpServletRequest request, HttpServletResponse response, IUrlCreator urlCreator,
            Object... optionalAttrs) {
        model.setHttpRequest(request);
        model.setHttpResponse(response);
        model.setUrlCreator(urlCreator);
        if (optionalAttrs != null) {
            for (Object obj : optionalAttrs) {
                model.optionalAttrs.add(obj);
            }
        }
        return model;
    }

    /**
     * Creates view model without creating the proxy object.
     * 
     * @param request
     * @param response
     * @param urlCreator
     * @param obj
     * @param optionalAttrs
     * @return
     */
    public static <T> BaseViewModel<T> createModelNoProxy(HttpServletRequest request,
            HttpServletResponse response, IUrlCreator urlCreator, T obj, Object... optionalAttrs) {
        BaseViewModel<T> model = new BaseViewModel<T>(obj);
        return initModelObj(model, request, response, urlCreator, optionalAttrs);
    }

    /**
     * Creates list of view model without creating the proxy objects.
     * 
     * @param request
     * @param response
     * @param urlCreator
     * @param objs
     * @param optionalAttrs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseViewModel<T>[] createModelNoProxy(HttpServletRequest request,
            HttpServletResponse response, IUrlCreator urlCreator, T[] objs, Object... optionalAttrs) {
        List<BaseViewModel<T>> result = new ArrayList<BaseViewModel<T>>();
        for (T obj : objs) {
            BaseViewModel<T> t = createModelNoProxy(request, response, urlCreator, obj,
                    optionalAttrs);
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray(new BaseViewModel[0]);
    }

    /**
     * Creates view model and returns the proxy object. The proxy object uses
     * the target object's {@link ClassLoader}, and implements interfaces
     * returned by target object's <code>getClass().getInterfaces()</code> call.
     * 
     * @param request
     * @param response
     * @param urlCreator
     * @param obj
     * @param optionalAttrs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, T obj, Object... optionalAttrs) {
        BaseViewModel<T> model = createModelNoProxy(request, response, urlCreator, obj,
                optionalAttrs);
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass()
                .getInterfaces(), model);
    }

    /**
     * Creates list of view models and return the list of proxy objects. Proxy
     * objects use the target object's {@link ClassLoader}, and implement
     * interfaces returned by target object's
     * <code>getClass().getInterfaces()</code> call.
     * 
     * @param request
     * @param response
     * @param urlCreator
     * @param objs
     * @param optionalAttrs
     * @return
     */
    public static <T> Object[] createModel(HttpServletRequest request,
            HttpServletResponse response, IUrlCreator urlCreator, T[] objs, Object... optionalAttrs) {
        List<T> result = new ArrayList<T>();
        for (T obj : objs) {
            T t = createModel(request, response, urlCreator, obj, optionalAttrs);
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T createModel(HttpServletRequest request, HttpServletResponse response,
            IUrlCreator urlCreator, Class<? extends BaseViewModel<T>> modelClazz,
            Class<?> targetClass, T obj, Object... optionalAttrs) throws SecurityException,
            NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        // interfaces that the proxy will implement
        List<Class<?>> proxyInterfaceList = new ArrayList<Class<?>>();

        // add interfaces that the target class implements/extends
        Class<?>[] interfacesTargetClass = targetClass.isInterface() ? new Class<?>[] { targetClass }
                : targetClass.getInterfaces();
        for (Class<?> interf : interfacesTargetClass) {
            if (!proxyInterfaceList.contains(interf)) {
                proxyInterfaceList.add(interf);
            }
        }

        // add interfaces that the model class implements/extends
        Class<?>[] interfacesModel = modelClazz.isInterface() ? new Class<?>[] { modelClazz }
                : modelClazz.getInterfaces();
        for (Class<?> interf : interfacesModel) {
            if (!proxyInterfaceList.contains(interf)) {
                proxyInterfaceList.add(interf);
            }
        }

        // add interface that the model object implements
        Class<?>[] interfacesTarget = obj.getClass().getInterfaces();
        for (Class<?> interf : interfacesTarget) {
            if (!proxyInterfaceList.contains(interf)) {
                proxyInterfaceList.add(interf);
            }
        }

        // create and initialize the model object
        Constructor<BaseViewModel<T>> c = (Constructor<BaseViewModel<T>>) modelClazz
                .getDeclaredConstructor(targetClass);
        c.setAccessible(true);
        BaseViewModel<T> model = c.newInstance(obj);
        initModelObj(model, request, response, urlCreator, optionalAttrs);

        // create a combined class loader for the proxy to use.
        CombinedClassLoader combinedClassLoader = new CombinedClassLoader();
        for (Class<?> clazz : proxyInterfaceList) {
            combinedClassLoader.addLoader(clazz);
        }
        return (T) Proxy.newProxyInstance(combinedClassLoader,
                proxyInterfaceList.toArray(new Class[0]), model);
        // return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(),
        // proxyInterfaceList.toArray(new Class[0]), model);
    }

    public static <T> Object[] createModel(HttpServletRequest request,
            HttpServletResponse response, IUrlCreator urlCreator,
            Class<? extends BaseViewModel<T>> modelClazz, Class<?> targetClass, T[] objs,
            Object... optionalAttrs) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        List<T> result = new ArrayList<T>();
        for (T obj : objs) {
            T t = createModel(request, response, urlCreator, modelClazz, targetClass, obj,
                    optionalAttrs);
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray();
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

    @SuppressWarnings("unchecked")
    protected <K> K getOptionalAttr(Class<K> clazz) {
        for (Object obj : optionalAttrs) {
            if (clazz.isAssignableFrom(obj.getClass())) {
                return (K) obj;
            }
        }
        return null;
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
