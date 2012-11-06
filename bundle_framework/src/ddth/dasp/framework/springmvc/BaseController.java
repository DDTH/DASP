package ddth.dasp.framework.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.common.utils.OsgiUtils;
import ddth.dasp.framework.utils.SpringUtils;

/**
 * Use this class as starting point for application SpringMVC's controllers.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version since v0.1.0
 */
public abstract class BaseController extends WebApplicationObjectSupport
		implements BundleContextAware {

	private BundleContext bundleContext;

	/**
	 * Gets a Spring bean by class.
	 * 
	 * @param clazz
	 * @return
	 */
	protected <T> T getBean(Class<T> clazz) {
		return SpringUtils.getBean(getApplicationContext(), clazz);
	}

	/**
	 * Gets an OSGi service by class.
	 * 
	 * @param clazz
	 * @return
	 */
	protected <T> T getService(Class<T> clazz) {
		return OsgiUtils.getService(bundleContext, clazz);
	}

	/**
	 * Gets the {@link BundleContext} instance.
	 * 
	 * @return
	 */
	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * Gets the associated {@link IRequestParser} instance.
	 * 
	 * @param request
	 * @return
	 */
	protected IRequestParser getRequestParser(HttpServletRequest request) {
		return (IRequestParser) request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
	}

	/**
	 * Gets the request module name.
	 * 
	 * @param request
	 * @return
	 */
	protected String getRequestModule(HttpServletRequest request) {
		IRequestParser rp = getRequestParser(request);
		return rp.getRequestModule();
	}

	/**
	 * Gets the request action name.
	 * 
	 * @param request
	 * @return
	 */
	protected String getRequestAction(HttpServletRequest request) {
		IRequestParser rp = getRequestParser(request);
		return rp.getRequestAction();
	}
}
