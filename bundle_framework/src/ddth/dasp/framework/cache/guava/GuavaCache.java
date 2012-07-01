package ddth.dasp.framework.cache.guava;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> implementation
 * of {@link ICache}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class GuavaCache implements ICache {

    private Cache<String, Object> cache;
    private String name;
    private long capacity;
    private long expireAfterWrite;
    private long expireAfterAccess;

    public GuavaCache() {
    }

    public GuavaCache(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        int numProcessores = Runtime.getRuntime().availableProcessors();
        CacheBuilder<Object, Object> cacheBuider = CacheBuilder.newBuilder();
        cacheBuider.concurrencyLevel(numProcessores);
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
        cache.cleanUp();
        cache = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
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
    public Object get(String key) {
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
