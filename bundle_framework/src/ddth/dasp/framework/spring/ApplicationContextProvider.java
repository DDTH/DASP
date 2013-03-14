package ddth.dasp.framework.spring;

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
 * ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
 * </pre>
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextProvider.applicationContext == null) {
            ApplicationContextProvider.applicationContext = applicationContext;
        } else {
            throw new IllegalStateException("ApplicationContext has already been set!");
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
