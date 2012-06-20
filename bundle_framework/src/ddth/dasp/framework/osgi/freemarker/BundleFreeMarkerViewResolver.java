package ddth.dasp.framework.osgi.freemarker;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * Use this {@link FreeMarkerViewResolver} to resove FreeMarkerView within a
 * bundle.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundleFreeMarkerViewResolver extends FreeMarkerViewResolver {
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		FreeMarkerConfig freemarkerConfig = getApplicationContext().getBean(
				FreeMarkerConfig.class);
		FreeMarkerView view = (FreeMarkerView) super.buildView(viewName);
		if (freemarkerConfig != null) {
			view.setConfiguration(freemarkerConfig.getConfiguration());
		}
		return view;
	}
}
