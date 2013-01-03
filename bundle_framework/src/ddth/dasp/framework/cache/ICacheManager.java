package ddth.dasp.framework.cache;

/**
 * Manager to manages {@link ICache} instances.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface ICacheManager {

    public final static String GLOBAL_KEY = "ALL_CACHE_MANAGERS";

    public final static long DEFAULT_CACHE_CAPACITY = 1000;
    public final static long DEFAULT_EXPIRE_AFTER_WRITE = 1800;
    public final static long DEFAULT_EXPIRE_AFTER_ACCESS = 1800;

    /**
     * Initializing method.
     */
    public void init();

    /**
     * Destruction method.
     */
    public void destroy();

    /**
     * Gets a cache by name.
     * 
     * @param name
     * @return an existing cache, <code>null</code> is returned if cache has not
     *         been created
     */
    public ICache getCache(String name);

    /**
     * Gets all current caches of this cache manager.
     * 
     * @return
     */
    public ICache[] getCaches();

    /**
     * Removes an existing cache by name.
     * 
     * @param name
     */
    public void removeCache(String name);

    /**
     * Creates a cache with default capacity and options. This method returns
     * the existing cache if such exists.
     * 
     * @param name
     * @return
     */
    public ICache createCache(String name);

    /**
     * Creates a cache with default options. This method returns the existing
     * cache if such exists.
     * 
     * @param name
     * @param capacity
     * @return
     */
    public ICache createCache(String name, long capacity);

    /**
     * Creates a cache. This method returns the existing cache if such exists.
     * 
     * @param name
     *            name of the cache to create
     * @param capacity
     *            long cache's maximum number of entries
     * @param expireAfterWrite
     *            expire entries after the specified number of seconds has
     *            passed since the entry was created
     * @param expireAfterAccess
     *            expire entries after the specified number of seconds has
     *            passed since the entry was last accessed by a read or a write
     * @return
     */
    public ICache createCache(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess);
}
