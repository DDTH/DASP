package ddth.wfp.servlet.struts.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.ServletContextAware;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.ActionSupport;

import ddth.wfp.servlet.mls.ILanguage;
import ddth.wfp.servlet.mls.LanguageManager;
import ddth.wfp.servlet.osgi.IOsgiBootstrap;
import ddth.wfp.servlet.runtime.menu.IMenuManager;
import ddth.wfp.servlet.runtime.menu.MenuItem;
import ddth.wfp.servlet.spring.ApplicationContextProvider;
import ddth.wfp.servlet.struts.model.ApplicationModel;
import ddth.wfp.servlet.struts.model.PageModel;
import ddth.wfp.utils.UrlUtils;
import ddth.wfp.utils.WfpConstants;

public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware,
        ServletContextAware {

    private static final long serialVersionUID = 1L;

    private HttpServletResponse httpServletResponse;
    private HttpServletRequest httpServletRequest;
    private ServletContext servletContext;
    private boolean requireAuthentication;

    private PageModel modelPage;
    private ApplicationModel modelApp;

    /**
     * Gets instance of {@link IOsgiBootstrap}.
     * 
     * @return
     */
    protected IOsgiBootstrap getOsgiBootstrap() {
        ApplicationContext ac = ApplicationContextProvider.getApplicationContext();
        return ac.getBean("IOsgiBootstrap", IOsgiBootstrap.class);
    }

    /**
     * Gets an OSGi service.
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> T getService(Class<T> clazz) {
        return getService(clazz, (String) null);
    }

    /**
     * Gets an OSGi service.
     * 
     * @param <T>
     * @param clazz
     * @param query
     * @return
     */
    public <T> T getService(Class<T> clazz, String query) {
        IOsgiBootstrap osgiBootstrap = getOsgiBootstrap();
        ServiceReference sref = osgiBootstrap.getServiceReference(clazz.getName(), query);
        if (sref != null) {
            try {
                return osgiBootstrap.getService(sref, clazz);
            } finally {
                osgiBootstrap.ungetServiceReference(sref);
            }
        }
        return null;
    }

    /**
     * Gets an OSGi service.
     * 
     * @param <T>
     * @param clazz
     * @param filter
     * @return
     */
    public <T> T getService(Class<T> clazz, Map<String, String> filter) {
        IOsgiBootstrap osgiBootstrap = getOsgiBootstrap();
        ServiceReference sref = osgiBootstrap.getServiceReference(clazz.getName(), filter);
        if (sref != null) {
            try {
                return osgiBootstrap.getService(sref, clazz);
            } finally {
                osgiBootstrap.ungetServiceReference(sref);
            }
        }
        return null;
    }

    /**
     * Gets the current requested URL.
     * 
     * @return String
     */
    public String getRequestUrl() {
        return httpServletRequest.getRequestURL().toString();
    }

    /**
     * Gets the current requested URI.
     * 
     * @return String
     */
    public String getRequestUri() {
        return httpServletRequest.getRequestURI();
    }

    /**
     * Gets the main menu.
     * 
     * @return
     */
    public MenuItem[] getMainMenu() {
        IMenuManager mm = getService(IMenuManager.class);
        return mm.getMainMenu();
    }

    /**
     * Model: the application information.
     * 
     * @return {@link ApplicationModel}
     */
    public ApplicationModel getApplication() {
        if (modelApp == null) {
            modelApp = new ApplicationModel(this);
        }
        return modelApp;
    }

    /**
     * Model: the html page.
     * 
     * @return {@link PageModel}
     */
    public PageModel getPage() {
        if (modelPage == null) {
            modelPage = new PageModel(this);
        }
        return modelPage;
    }

    /**
     * Model: the language pack.
     * 
     * @return
     */
    public ILanguage getLanguage() {
        // TODO: get the locale/language pack from session?
        // TODO: default language pack?
        return LanguageManager.getLanguage(new Locale("en"));
    }

    /**
     * Model: the URI pointing to the home page.
     * 
     * @return String
     */
    public String getUriHome() {
        return getServletContext().getContextPath();
    }

    /**
     * Model: the URI that invokes the form loading action.
     * 
     * Note: the returned URI must be in the format that form name can be
     * appended to the end of the URI to form the complete URI to load the
     * specified form.
     * 
     * @return
     */
    public String getUriFormLoad() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(WfpConstants.URL_PARAM_FORM, "");
        String result = UrlUtils.createUrl(new String[] { WfpConstants.ACTION_DOMAIN_FORM,
                WfpConstants.ACTION_FORM_LOAD }, params);
        return result;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Gets the associated request.
     * 
     * @return HttpServletRequest
     */
    public HttpServletRequest getServletRequest() {
        return httpServletRequest;
    }

    @Override
    public void setServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * Gets the associated response.
     * 
     * @return HttpServletResponse
     */
    public HttpServletResponse getServletResponse() {
        return httpServletResponse;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Gets the http session instance.
     * 
     * @param forceCreate
     * @return
     */
    public HttpSession getHttpSession(boolean forceCreate) {
        return httpServletRequest.getSession(forceCreate);
    }

    /**
     * Gets the associated servlet context.
     * 
     * @return ServletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    public boolean isRequireAuthentication() {
        return requireAuthentication;
    }

    public void setRequireAuthentication(boolean requireAuthentication) {
        this.requireAuthentication = requireAuthentication;
    }
}
