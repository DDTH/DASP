package ddth.dasp.framework.cache.guava;

import ddth.dasp.framework.cache.AbstractCacheManager;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> implementation
 * of {@link ICacheManager}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class GuavaCacheManager extends AbstractCacheManager {

    /**
     * {@inheritDoc}
     */
    @Override
    protected GuavaCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        GuavaCache cache = new GuavaCache(name);
        cache.setCapacity(capacity > 0 ? capacity : getDefaultCacheCapacity());
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setExpireAfterWrite(expireAfterWrite);
        cache.init();
        return cache;
    }
}
