package ddth.dasp.framework.bo;

import ddth.dasp.framework.cache.CacheEntry;
import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.cache.ICacheManager;
import ddth.dasp.framework.cache.NullValue;

/**
 * Cache-enabled Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class CacheBoManager extends BaseBoManager {

    public final static long DEFAULT_CACHE_NULL_EXPIRY = 3600;

    private ICacheManager cacheManager, cacheManagerForNull;
    private String cacheName = CacheBoManager.class.getName(),
            cacheNameForNull = CacheBoManager.class.getName() + "_null";
    private ICache cache, cacheForNull;
    private long cacheCapcity = 0, cacheCapcityForNull = 0, expireAfterWrite = -1,
            expireAfterAccess = -1;
    private boolean cacheNull = false;
    private long cacheNullExpiry = DEFAULT_CACHE_NULL_EXPIRY;

    public void init() {
        super.init();
        cache = getCache();
        cacheForNull = getCacheForNull();
    }

    public ICacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public ICacheManager getCacheManagerForNull() {
        return cacheManagerForNull;
    }

    public void setCacheManagerForNull(ICacheManager cacheManagerForNull) {
        this.cacheManagerForNull = cacheManagerForNull;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheNameForNull() {
        return cacheNameForNull;
    }

    public void setCacheNameForNull(String cacheNameForNull) {
        this.cacheNameForNull = cacheNameForNull;
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
            // if (cacheCapcity <= 0) {
            // cacheCapcity = Integer.MAX_VALUE;
            // }
            cache = cacheManager.createCache(cacheName, cacheCapcity, expireAfterWrite,
                    expireAfterAccess);
        }
        return cache;
    }

    public ICache getCacheForNull() {
        if (cacheForNull == null && cacheEnabled() && cacheNull) {
            if (cacheCapcityForNull <= 0) {
                cacheCapcityForNull = Integer.MAX_VALUE;
            }
            if (cacheManagerForNull == null) {
                cacheManagerForNull = cacheManager;
            }
            cacheForNull = cacheManagerForNull.createCache(cacheNameForNull, cacheCapcityForNull,
                    cacheNullExpiry, -1);
        }
        return cacheForNull;
    }

    /**
     * Deletes all cache entries.
     */
    protected void flushCache() {
        if (!cacheEnabled()) {
            return;
        }

        ICache cache = getCache();
        if (cache != null) {
            cache.deleteAll();
        }
        ICache cacheForNull = getCacheForNull();
        if (cacheForNull != null && cacheForNull != cache) {
            cacheForNull.deleteAll();
        }
    }

    /**
     * Deletes an entry from cache.
     * 
     * @param key
     */
    protected void deleteFromCache(String key) {
        if (!cacheEnabled()) {
            return;
        }

        ICache cache = getCache();
        if (cache != null) {
            cache.delete(key);
        }
        ICache cacheForNull = getCacheForNull();
        if (cacheForNull != null && cacheForNull != cache) {
            cacheForNull.delete(key);
        }
    }

    /**
     * Puts an entry to cache, with default expiries.
     * 
     * @param key
     * @param value
     */
    protected void putToCache(String key, Object value) {
        // if (!cacheEnabled()) {
        // return;
        // }

        if (value instanceof CacheEntry) {
            CacheEntry ce = (CacheEntry) value;
            putToCache(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
        } else {
            putToCache(key, value, expireAfterWrite, expireAfterAccess);
        }

        // if (value != null) {
        // ICache cache = getCache();
        // if (cache != null) {
        // cache.set(key, value);
        // }
        // } else if (cacheNull) {
        // ICache cacheForNull = getCacheForNull();
        // if (cacheForNull != null) {
        // CacheEntry cacheEntry = new CacheEntry(key, new NullValue(),
        // cacheNullExpiry, -1);
        // cacheForNull.set(key, cacheEntry);
        // }
        // }
    }

    /**
     * Puts an entry to cache, with specified expiries.
     * 
     * @param key
     * @param value
     * @param expireAfterWrite
     * @param expireAfterAccess
     */
    protected void putToCache(String key, Object value, long expireAfterWrite,
            long expireAfterAccess) {
        if (!cacheEnabled()) {
            return;
        }

        if (value != null) {
            ICache cache = getCache();
            if (cache != null) {
                cache.set(key, value, expireAfterWrite, expireAfterAccess);
            }
        } else if (cacheNull) {
            ICache cacheForNull = getCacheForNull();
            if (cacheForNull != null) {
                long ttl = cacheNullExpiry > 0 ? cacheNullExpiry
                        : (expireAfterWrite > 0 ? expireAfterWrite : -1);
                CacheEntry cacheEntry = new CacheEntry(key, new NullValue(), ttl, -1);
                cacheForNull.set(key, cacheEntry, ttl, -1);
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
        if (!cacheEnabled()) {
            return null;
        }

        ICache cache = getCache();
        Object value = cache.get(key);
        if (value == null && cacheNull) {
            ICache cacheForNull = getCacheForNull();
            value = cacheForNull.get(key);
        }
        if (value instanceof CacheEntry && ((CacheEntry) value).isExpired()) {
            return null;
        }
        return value;
    }
}
