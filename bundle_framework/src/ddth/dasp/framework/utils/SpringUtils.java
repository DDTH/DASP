package ddth.dasp.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Spring-related utility class.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class SpringUtils {
    /**
     * Gets a bean by its class.
     * 
     * @param <T>
     * @param appContext
     * @param clazz
     * @return
     */
    public static <T> T getBean(ApplicationContext appContext, Class<T> clazz) {
        try {
            return appContext.getBean(clazz);
        } catch (BeansException e) {
            return null;
        }
    }
}
