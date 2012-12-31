package ddth.dasp.hetty.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;

import ddth.dasp.framework.osgi.IServiceAutoRegister;
import ddth.dasp.hetty.IRequestActionHandler;
import ddth.dasp.hetty.IUrlCreator;
import ddth.dasp.hetty.message.HettyProtoBuf;
import ddth.dasp.hetty.message.IRequestParser;
import ddth.dasp.hetty.mvc.view.IView;
import ddth.dasp.hetty.mvc.view.IViewResolver;
import ddth.dasp.hetty.mvc.view.RedirectView;
import ddth.dasp.hetty.qnt.ITopicPublisher;

/**
 * This action handler implements the following workflow:
 * <ul>
 * <li>
 * {@link #internalHandleRequest(HettyProtoBuf.Request, ITopicPublisher)} is
 * called to handle the request. If a view object is returned:
 * <ul>
 * <li>
 * {@link #resolveVew(HettyProtoBuf.Request, String)} is called to resolve the
 * view.</li>
 * <li>If the view is resolved, {@link #buildViewModel()} is called to build the
 * view model</li>
 * <li>If the view is resolved, method
 * {@link IView#render(HettyProtoBuf.Request, Object, ITopicPublisher)} is call
 * to render view.</li></li>
 * </ul>
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 */
public abstract class AbstractActionHandler implements IRequestActionHandler, IServiceAutoRegister,
        ApplicationContextAware, BundleContextAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractActionHandler.class);

    private ApplicationContext appContext;
    private BundleContext bundleContext;

    private Properties properties;
    private IViewResolver viewResolver;
    private IRequestParser requestParser;
    private IUrlCreator urlCreator;
    private String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public void setViewResolver(IViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    protected IViewResolver getViewResolver() {
        if (viewResolver == null) {
            try {
                viewResolver = appContext.getBean(IViewResolver.class);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return viewResolver;
    }

    public void setRequestParser(IRequestParser requestParser) {
        this.requestParser = requestParser;
    }

    protected IRequestParser getRequestParser() {
        if (requestParser == null) {
            try {
                requestParser = appContext.getBean(IRequestParser.class);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return requestParser;
    }

    public void setUrlCreator(IUrlCreator urlCreator) {
        this.urlCreator = urlCreator;
    }

    protected IUrlCreator getUrlCreator() {
        if (urlCreator == null) {
            try {
                urlCreator = appContext.getBean(IUrlCreator.class);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return urlCreator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return IRequestActionHandler.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleRequest(HettyProtoBuf.Request request, ITopicPublisher topicPublisher)
            throws Exception {
        Object view = internalHandleRequest(request, topicPublisher);
        if (view == null) {
            return;
        }
        Object oldView = view;
        if (!(view instanceof IView)) {
            String viewName = view.toString();
            view = resolveVew(request, viewName);
        }
        if (view instanceof IView) {
            Object model = view instanceof RedirectView ? null : buildViewModel();
            ((IView) view).render(request, model, topicPublisher);
        } else {
            String msg = "Can not resolve view for [" + oldView + "]!";
            throw new Exception(msg);
        }
    }

    /**
     * This method simply returns an empty {@link Map}.
     * 
     * @return
     */
    protected Map<String, Object> buildViewModel() {
        Map<String, Object> model = new HashMap<String, Object>();
        return model;
    }

    /**
     * Sub-class to implement this method. It will be called by
     * {@link #handleRequest(ddth.dasp.hetty.message.HettyProtoBuf.Request, ITopicPublisher)}
     * .
     * 
     * @param request
     * @param topicPublisher
     * @return
     */
    protected abstract Object internalHandleRequest(HettyProtoBuf.Request request,
            ITopicPublisher topicPublisher);

    /**
     * Resolves a view name to {@link IView} object.
     * 
     * @param request
     * @param viewName
     * @return
     */
    protected IView resolveVew(HettyProtoBuf.Request request, String viewName) {
        Map<String, String> replacements = new HashMap<String, String>();
        IViewResolver viewResolver = getViewResolver();
        return viewResolver.resolveView(viewName, replacements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    protected ApplicationContext getApplicationContext() {
        return appContext;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }
}
