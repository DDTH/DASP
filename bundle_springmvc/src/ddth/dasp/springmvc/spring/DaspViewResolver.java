package ddth.dasp.springmvc.spring;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.osgi.IOsgiBootstrap;

public class DaspViewResolver extends AbstractCachingViewResolver {

    private Logger LOGGER = LoggerFactory.getLogger(DaspViewResolver.class);
    private final static Class<ViewResolver> SERVICE_CLASS = ViewResolver.class;
    public final static String REDIRECT_WITH_MODELS = "redirectModels:";

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        if (viewName == null) {
            return null;
        }
        String[] tokens = viewName.split(":", 2);
        if (tokens.length != 2) {
            String msg = "Invalid view name, must be in format <module_name>:<view_name>!";
            LOGGER.warn(msg);
            return null;
        }

        String moduleName = tokens[0];

        if ("redirect".equalsIgnoreCase(moduleName) || "forward".equalsIgnoreCase(moduleName)) {
            return new RedirectView(tokens[1], true, true, false);
        } else if (REDIRECT_WITH_MODELS.equalsIgnoreCase(moduleName)) {
            return new RedirectView(tokens[1]);
        }

        if (StringUtils.isBlank(moduleName)) {
            moduleName = "home";
        }

        IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
        if (osgiBootstrap == null) {
            String msg = "Instance of [" + IOsgiBootstrap.class + " not found]!";
            LOGGER.warn(msg);
            return null;
        }
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("Module", moduleName);
        ViewResolver viewResolver = osgiBootstrap.getService(SERVICE_CLASS, filter);
        if (viewResolver == null) {
            String msg = "No view resolver found for module [" + moduleName + "]!";
            LOGGER.warn(msg);
            return null;
        }

        View result = viewResolver.resolveViewName(tokens[1], locale);
        if (LOGGER.isDebugEnabled()) {
            String msg = "Found [" + result + "] for module [" + moduleName + "].";
            LOGGER.debug(msg);
        }
        return result;
    }
}
