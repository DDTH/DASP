package ddth.dasp.framework.cache;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractCache implements ICache {

    private String name;
    private long capacity;
    private long expireAfterWrite;
    private long expireAfterAccess;
    private AtomicLong hits = new AtomicLong(), misses = new AtomicLong();

    public AbstractCache() {
    }

    public AbstractCache(String name) {
        this.name = name;
    }

    public AbstractCache(String name, long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public AbstractCache(String name, long capacity, long expireAfterWrite, long expireAfterAccess) {
        this.name = name;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // EMPTY
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
    public long getHits() {
        return hits.get();
    }

    protected long incHits() {
        return hits.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMisses() {
        return misses.get();
    }

    protected long incMisses() {
        return misses.incrementAndGet();
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
    public Object get(String key) {
        Object value = internalGet(key);
        if (value instanceof CacheEntry) {
            if (((CacheEntry) value).isExpired()) {
                incMisses();
                return null;
            } else {
                // update entry's access timestamp
                ((CacheEntry) value).touch();
                set(key, value);
            }
        }
        if (value == null) {
            incMisses();
        } else {
            incHits();
        }
        return value;
    }

    /**
     * Gets an entry from cache. Sub-class overrides this method to actually
     * retrieve entries from cache.
     * 
     * @param key
     * @return
     */
    protected abstract Object internalGet(String key);
}
