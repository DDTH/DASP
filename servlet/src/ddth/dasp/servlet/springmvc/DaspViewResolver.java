package ddth.dasp.servlet.springmvc;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

public class DaspViewResolver extends AbstractCachingViewResolver {

	private Logger LOGGER = LoggerFactory.getLogger(DaspViewResolver.class);

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		LOGGER.debug("Loading view for [" + viewName + "/" + locale + "]...");
		// TODO Auto-generated method stub
		return null;
	}

}
