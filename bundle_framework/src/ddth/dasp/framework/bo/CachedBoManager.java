package ddth.dasp.framework.bo;

import ddth.dasp.framework.cache.CacheEntry;
import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * Cache-enabled Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class CachedBoManager extends BaseBoManager {

    public final static long DEFAULT_CACHE_NULL_EXPIRY = 3600;

    private ICacheManager cacheManager;
    private String cacheName = CachedBoManager.class.getName();
    private ICache cache = null;
    private long cacheCapcity = 0, expireAfterWrite = -1, expireAfterAccess = -1;
    private boolean cacheNull = false;
    private long cacheNullExpiry = DEFAULT_CACHE_NULL_EXPIRY;

    public void init() {
        super.init();
        if (cacheManager != null) {
            if (cacheCapcity <= 0) {
                cacheCapcity = Integer.MAX_VALUE;
            }
            cache = cacheManager.createCache(cacheName, cacheCapcity, expireAfterWrite,
                    expireAfterAccess);
        }
    }

    public ICacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public boolean isCacheNull() {
        return cacheNull;
    }

    public void setCacheNull(boolean cacheNull) {
        this.cacheNull = cacheNull;
    }

    protected boolean cacheEnabled() {
        return cacheManager != null;
    }

    public ICache getCache() {
        if (cache == null && cacheManager != null) {
            if (cacheCapcity <= 0) {
                cacheCapcity = Integer.MAX_VALUE;
            }
            cache = cacheManager.createCache(cacheName, cacheCapcity, expireAfterWrite,
                    expireAfterAccess);
        }
        return cache;
    }

    /**
     * Deletes all cache entries.
     */
    protected void flushCache() {
        ICache cache = getCache();
        if (cache != null) {
            cache.deleteAll();
        }
    }

    /**
     * Deletes an entry from cache.
     * 
     * @param key
     */
    protected void deleteFromCache(String key) {
        ICache cache = getCache();
        if (cache != null) {
            cache.delete(key);
        }
    }

    /**
     * Puts an entry to cache.
     * 
     * @param key
     * @param value
     */
    protected void putToCache(String key, Object value) {
        ICache cache = getCache();
        if (cache != null) {
            if (value == null) {
                if (cacheNull) {
                    CacheEntry cacheEntry = new CacheEntry(key, value, cacheNullExpiry, -1);
                    cache.set(key, cacheEntry);
                }
            } else {
                cache.set(key, value);
            }
        }
    }

    /**
     * Gets an entry from cache.
     * 
     * @param key
     * @return
     */
    protected Object getFromCache(String key) {
        ICache cache = getCache();
        if (cache != null) {
            Object value = cache.get(key);
            if (value instanceof CacheEntry && ((CacheEntry) value).isExpired()) {
                return null;
            }
            return value;
        }
        return null;
    }
}
