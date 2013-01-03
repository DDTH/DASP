package ddth.dasp.framework.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.id.IdGenerator;

public abstract class AbstractCacheManager implements ICacheManager {

    public final static String CACHE_PROP_CAPACITY = "cache.capacity";
    public final static String CACHE_PROP_EXPIRE_AFTER_WRITE = "cache.expireAfterWrite";
    public final static String CACHE_PROP_EXPIRE_AFTER_ACCESS = "cache.expireAfterAccess";

    private long defaultCacheCapacity = ICacheManager.DEFAULT_CACHE_CAPACITY;
    private long defaultExpireAfterAccess = ICacheManager.DEFAULT_EXPIRE_AFTER_ACCESS;
    private long defaultExpireAfterWrite = ICacheManager.DEFAULT_EXPIRE_AFTER_WRITE;
    private ConcurrentMap<String, ICache> caches;
    private Map<String, Properties> cacheProperties;
    private String cacheNamePrefix;
    private String ID = IdGenerator.getInstance(IdGenerator.getMacAddr()).generateId64Hex();

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        int numProcessores = Runtime.getRuntime().availableProcessors();
        MapMaker mm = new MapMaker();
        mm.concurrencyLevel(numProcessores);
        caches = mm.makeMap();
        synchronized (ICacheManager.class) {
            Object temp = DaspGlobal.getGlobalVar(ICacheManager.GLOBAL_KEY);
            if (!(temp instanceof Map)) {
                temp = new HashMap<String, ICacheManager>();
                DaspGlobal.setGlobalVar(ICacheManager.GLOBAL_KEY, temp);
            }
            Map<String, ICacheManager> allCacheManagers = (Map<String, ICacheManager>) temp;
            allCacheManagers.put(ID, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        synchronized (ICacheManager.class) {
            Object temp = DaspGlobal.getGlobalVar(ICacheManager.GLOBAL_KEY);
            if (!(temp instanceof Map)) {
                temp = new HashMap<String, ICacheManager>();
                DaspGlobal.setGlobalVar(ICacheManager.GLOBAL_KEY, temp);
            }
            Map<String, ICacheManager> allCacheManagers = (Map<String, ICacheManager>) temp;
            allCacheManagers.remove(ID);
        }

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

    protected String buildCacheName(String cacheName) {
        return cacheNamePrefix != null ? cacheNamePrefix + cacheName : cacheName;
    }

    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    public void setCacheNamePrefix(String cacheNamePrefix) {
        this.cacheNamePrefix = cacheNamePrefix;
    }

    public long getDefaultCacheCapacity() {
        return defaultCacheCapacity;
    }

    public void setDefaultCacheCapacity(long defaultCacheCapacity) {
        this.defaultCacheCapacity = defaultCacheCapacity;
    }

    public long getDefaultExpireAfterAccess() {
        return defaultExpireAfterAccess;
    }

    public void setDefaultExpireAfterAccess(long defaultExpireAfterAccess) {
        this.defaultExpireAfterAccess = defaultExpireAfterAccess;
    }

    public long getDefaultExpireAfterWrite() {
        return defaultExpireAfterWrite;
    }

    public void setDefaultExpireAfterWrite(long defaultExpireAfterWrite) {
        this.defaultExpireAfterWrite = defaultExpireAfterWrite;
    }

    public void setCacheProperties(Map<String, Properties> cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    /**
     * Gets a cache's properties
     * 
     * @param name
     * @return
     */
    protected Properties getCacheProperties(String name) {
        return cacheProperties != null ? cacheProperties.get(name) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICache getCache(String name) {
        return caches.get(buildCacheName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public ICache[] getCaches() {
        List<ICache> result = new ArrayList<ICache>();
        for (Entry<String, ICache> entry : caches.entrySet()) {
            result.add(entry.getValue());
        }
        return result.toArray(new ICache[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCache(String name) {
        String cacheName = buildCacheName(name);
        ICache cache = caches.get(cacheName);
        if (cache != null) {
            try {
                cache.destroy();
            } finally {
                caches.remove(cacheName);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public ICache createCache(String name) {
        return createCache(name, defaultCacheCapacity, defaultExpireAfterWrite,
                defaultExpireAfterAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public ICache createCache(String name, long capacity) {
        return createCache(name, capacity, defaultExpireAfterWrite, defaultExpireAfterAccess);
    }

    @Override
    synchronized public ICache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        String cacheName = buildCacheName(name);
        ICache cache = caches.get(cacheName);
        if (cache == null) {
            // check if custom cache settings exist
            long cacheCapacity = capacity;
            long cacheExpireAfterWrite = expireAfterWrite;
            long cacheExpireAfterAccess = expireAfterAccess;
            // yup, use "name" here (not "cacheName) is correct and intended
            Properties cacheProps = getCacheProperties(name);
            if (cacheProps != null) {
                try {
                    cacheCapacity = Long.parseLong(CACHE_PROP_CAPACITY);
                } catch (Exception e) {
                    cacheCapacity = capacity;
                }
                try {
                    cacheExpireAfterWrite = Long.parseLong(CACHE_PROP_EXPIRE_AFTER_WRITE);
                } catch (Exception e) {
                    cacheExpireAfterWrite = expireAfterWrite;
                }
                try {
                    cacheExpireAfterAccess = Long.parseLong(CACHE_PROP_EXPIRE_AFTER_ACCESS);
                } catch (Exception e) {
                    cacheExpireAfterAccess = expireAfterAccess;
                }
            }
            cache = createCacheInternal(cacheName, cacheCapacity, cacheExpireAfterWrite,
                    cacheExpireAfterAccess);
            caches.put(cacheName, cache);
        }
        return cache;
    }

    /**
     * Creates a new cache instance. Convenient for sub-class to override.
     * 
     * @param name
     * @param capacity
     * @param expireAfterWrite
     * @param expireAfterAccess
     * @return
     */
    protected abstract ICache createCacheInternal(String name, long capacity,
            long expireAfterWrite, long expireAfterAccess);
}
