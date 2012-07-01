package ddth.dasp.framework.cache.guava;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a> implementation
 * of {@link ICacheManager}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class GuavaCacheManager implements ICacheManager {

    private long defaultCacheCapacity = ICacheManager.DEFAULT_CACHE_CAPACITY;
    private long defaultExpireAfterAccess = ICacheManager.DEFAULT_EXPIRE_AFTER_ACCESS;
    private long defaultExpireAfterWrite = ICacheManager.DEFAULT_EXPIRE_AFTER_WRITE;
    private ConcurrentMap<String, ICache> caches;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        int numProcessores = Runtime.getRuntime().availableProcessors();
        MapMaker mm = new MapMaker();
        mm.concurrencyLevel(numProcessores);
        caches = mm.makeMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (caches != null) {
            try {
                Iterator<Entry<String, ICache>> it = caches.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, ICache> entry = it.next();
                    entry.getValue().destroy();
                }
            } finally {
                caches.clear();
            }
            caches = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICache getCache(String name) {
        return caches.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICache createCache(String name, long capacity) {
        return createCache(name, capacity, defaultExpireAfterWrite, defaultExpireAfterAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuavaCache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        GuavaCache cache = new GuavaCache(name);
        cache.setCapacity(capacity > 0 ? capacity : defaultCacheCapacity);
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setExpireAfterWrite(expireAfterWrite);
        cache.init();
        caches.put(name, cache);
        return cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCache(String name) {
        ICache cache = getCache(name);
        if (cache != null) {
            try {
                cache.destroy();
            } finally {
                caches.remove(name);
            }
        }
    }
}
