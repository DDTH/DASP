package ddth.dasp.framework.cache.guava;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ddth.dasp.framework.cache.AbstractCache;
import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> implementation
 * of {@link ICache}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class GuavaCache extends AbstractCache implements ICache {

    private Cache<String, Object> cache;

    public GuavaCache() {
    }

    public GuavaCache(String name) {
        super(name);
    }

    public GuavaCache(String name, long capacity) {
        super(name, capacity);
    }

    public GuavaCache(String name, long capacity, long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        int numProcessores = Runtime.getRuntime().availableProcessors();
        CacheBuilder<Object, Object> cacheBuider = CacheBuilder.newBuilder();
        cacheBuider.concurrencyLevel(numProcessores);
        long capacity = getCapacity();
        long expireAfterAccess = getExpireAfterAccess();
        long expireAfterWrite = getExpireAfterWrite();
        cacheBuider.maximumSize(capacity > 0 ? capacity : ICacheManager.DEFAULT_CACHE_CAPACITY);
        if (expireAfterAccess > 0) {
            cacheBuider.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
        } else if (expireAfterWrite > 0) {
            cacheBuider.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
        } else {
            cacheBuider.expireAfterAccess(ICacheManager.DEFAULT_EXPIRE_AFTER_ACCESS,
                    TimeUnit.SECONDS);
        }
        cache = cacheBuider.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            cache.cleanUp();
            cache = null;
        } finally {
            super.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return cache.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        cache.put(key, entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        cache.invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }
}
