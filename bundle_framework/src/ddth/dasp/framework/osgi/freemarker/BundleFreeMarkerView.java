package ddth.dasp.framework.osgi.freemarker;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.SimpleHash;

/**
 * Use this {@link FreeMarkerView} within a bundle.
 * 
 * This {@link FreeMarkerView} removes dependencies to
 * {@link WebApplicationContext}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class BundleFreeMarkerView extends FreeMarkerView {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isContextRequired() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) {
        SimpleHash fmModel = new SimpleHash(model);
        return fmModel;
    }
}
