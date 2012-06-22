package ddth.dasp.servlet.springmvc;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class DaspViewResolver {// extends AbstractCachingViewResolver {

	private Logger LOGGER = LoggerFactory.getLogger(DaspViewResolver.class);
	private final static Class<ViewResolver> SERVICE_CLASS = ViewResolver.class;

	// @Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		return null;
		// if (viewName == null) {
		// return null;
		// }
		// String[] tokens = viewName.split(":", 2);
		// if (tokens.length != 2) {
		// String msg =
		// "Invalid view name, must be in format <module_name>:<view_name>!";
		// LOGGER.warn(msg);
		// return null;
		// }
		//
		// String moduleName = tokens[0];
		// if (StringUtils.isBlank(moduleName)) {
		// moduleName = "home";
		// }
		//
		// IOsgiBootstrap osgiBootstrap = SpringUtils.getBean(
		// getApplicationContext(), IOsgiBootstrap.class);
		// if (osgiBootstrap == null) {
		// String msg = "Instance of [" + IOsgiBootstrap.class
		// + " not found]!";
		// LOGGER.warn(msg);
		// return null;
		// }
		// Map<String, String> filter = new HashMap<String, String>();
		// filter.put("Module", moduleName);
		// ViewResolver viewResolver = osgiBootstrap.getService(SERVICE_CLASS,
		// filter);
		// if (viewResolver == null) {
		// String msg = "No view resolver found for module [" + moduleName
		// + "]!";
		// LOGGER.warn(msg);
		// return null;
		// }
		//
		// View result = viewResolver.resolveViewName(tokens[1], locale);
		// if (LOGGER.isDebugEnabled()) {
		// String msg = "Found [" + result + "] for module [" + moduleName
		// + "].";
		// LOGGER.debug(msg);
		// }
		// return result;
	}
}
