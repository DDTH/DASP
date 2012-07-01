package ddth.dasp.framework.cache;

/**
 * Manager to manages {@link ICache} instances.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface ICacheManager {

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
     * @return
     */
    public ICache getCache(String name);

    /**
     * Removes a cache by name.
     * 
     * @param name
     * @return
     */
    public void removeCache(String name);

    /**
     * Creates a cache with default options.
     * 
     * @param name
     * @param capacity
     */
    public ICache createCache(String name, long capacity);

    /**
     * Creates a cache.
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
