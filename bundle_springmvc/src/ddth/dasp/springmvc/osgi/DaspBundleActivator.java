package ddth.dasp.springmvc.osgi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.IRequestHandler;
import ddth.dasp.framework.osgi.BaseSpringBundleActivator;
import ddth.dasp.springmvc.DaspBundleConstants;

public class DaspBundleActivator extends BaseSpringBundleActivator {

    private final static String[] springConfigFiles = new String[] { "META-INF/osgispring/*.xml" };
    private DispatcherServlet dispatcherServlet;
    private List<Object[]> serviceInfoList;

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Object[]> getServiceInfoList() {
        if (serviceInfoList == null) {
            serviceInfoList = new ArrayList<Object[]>();
            Object[] serviceInfo = new Object[] { IRequestHandler.class.getName(),
                    new DaspRequestHandler() };
            serviceInfoList.add(serviceInfo);
        }
        return serviceInfoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getSpringConfigFiles() {
        return springConfigFiles;
    }

    private class DaspRequestHandler implements IRequestHandler {
        @Override
        public void handleRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            dispatcherServlet.service(request, response);
        }
    }

    private static class EmptyServletConfig implements ServletConfig {
        @SuppressWarnings("rawtypes")
        private Vector params = new Vector();

        @Override
        public String getServletName() {
            return "daspSpringMvc";
        }

        @Override
        public ServletContext getServletContext() {
            return DaspGlobal.getServletContext();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Enumeration getInitParameterNames() {
            return params.elements();
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }
    }

    protected void destroyDispatcherServlet() {
        try {
            dispatcherServlet.destroy();
        } catch (Exception e) {
            // empty
        }
    }

    protected void initDispatcherServlet() throws ServletException {
        ServletConfig servletConfig = new EmptyServletConfig();
        XmlWebApplicationContext wac = new XmlWebApplicationContext();
        wac.setParent(getApplicationContext());
        wac.setServletContext(DaspGlobal.getServletContext());
        wac.setServletConfig(servletConfig);
        wac.setConfigLocations(new String[0]);
        dispatcherServlet = new DispatcherServlet(wac);
        dispatcherServlet.init(servletConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        initDispatcherServlet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        destroyDispatcherServlet();
        super.stop(bundleContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModuleName() {
        return DaspBundleConstants.MODULE_NAME;
    }
}
