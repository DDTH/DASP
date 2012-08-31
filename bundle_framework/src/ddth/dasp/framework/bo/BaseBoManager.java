package ddth.dasp.framework.bo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * Uses this class as starting point for Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseBoManager implements BundleContextAware {
    private Map<Class<?>, Map<String, Object[]>> dataMappings;
    private BundleContext bundleContext;

    /**
     * Initializing method
     */
    public void init() {
        // EMPTY
    }

    /**
     * Destroying method
     */
    public void destroy() {
        // EMPTY
    }

    /**
     * Sets data mappings for BO classes.
     * 
     * @param dataMappings
     * @see IBo#getDataMappings()
     */
    public void setDataMappings(Map<Class<?>, Map<String, Object[]>> dataMappings) {
        this.dataMappings = dataMappings;
    }

    /**
     * Gets data mappings for BO classes
     * 
     * @return
     * @see IBo#getDataMappings()
     */
    public Map<Class<?>, Map<String, Object[]>> getDataMappings() {
        return dataMappings;
    }

    /**
     * Gets data mapping associated with a BO class.
     * 
     * @param clazz
     * @return
     * @see IBo#getDataMappings()
     */
    public Map<String, Object[]> getDataMappings(Class<?> clazz) {
        return dataMappings != null ? dataMappings.get(clazz) : null;
    }

    /**
     * Creates a new instance of a BO.
     * 
     * This methods automatically set data mapping for the newly created BO if
     * applicable.
     * 
     * @param clazz
     * @return
     * @see IBo#getDataMappings()
     */
    protected <T extends IBo> T createBusinessObject(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getConstructor();
            T bo = c.newInstance();
            if (bo instanceof BaseBo) {
                Map<String, Object[]> dataMappings = getDataMappings(clazz);
                if (dataMappings != null) {
                    ((BaseBo) bo).setDataMappings(dataMappings);
                }
            }
            return bo;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }
}
