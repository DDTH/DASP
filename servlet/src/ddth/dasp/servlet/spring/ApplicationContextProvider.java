package ddth.dasp.servlet.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class provide an application-wide access to Spring's ApplicationContext.
 * Use {@link ApplicationContextProvider#getApplicationContext()} to obtain the
 * ApplicationContext instance.
 * <p>
 * Note: This class is a Spring bean and must be initialized in the
 * Spring-Configuration file:
 * </p>
 * 
 * <pre>
 * &lt;bean id="contextApplicationContextProvider" class="...ApplicationContextProvider" /&gt;
 * </pre>
 * <p>
 * In code:
 * </p>
 * 
 * <pre>
 * ApplicationContext appContext = ApplicationContextProvider
 * 		.getApplicationContext();
 * </pre>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext appContext;

	/**
	 * Gets the ApplicationContext instance.
	 * 
	 * @return ApplicationContext
	 */
	public final static ApplicationContext getApplicationContext() {
		return ApplicationContextProvider.appContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ApplicationContextProvider.appContext = appContext;
	}
}
