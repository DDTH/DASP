package ddth.dasp.hetty.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

public class HettyBootstrapServlet extends GenericServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(HettyBootstrapServlet.class);
    private AbstractApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig servletConfig = getServletConfig();

        String springConfigFile = servletConfig.getInitParameter("springConfigFile");
        // URL url = this.getServletContext().getResource(springConfigFile);
        Resource resource = new ServletContextResource(getServletContext(), springConfigFile);
        applicationContext = new GenericXmlApplicationContext(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (applicationContext != null) {
                applicationContext.close();
                applicationContext = null;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        super.destroy();
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException,
            IOException {
    }
}
