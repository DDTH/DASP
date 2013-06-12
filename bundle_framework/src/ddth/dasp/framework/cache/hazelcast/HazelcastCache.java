package ddth.dasp.framework.cache.hazelcast;

import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.IMap;

import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.framework.cache.AbstractCache;
import ddth.dasp.framework.cache.CacheEntry;
import ddth.dasp.framework.cache.ICache;

/**
 * <a href="http://www.hazelcast.com/">Hazelcast</a> implementation of
 * {@link ICache}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class HazelcastCache extends AbstractCache implements ICache {

    private IHazelcastClientFactory hazelcastClientFactory;
    private long timeToLiveSeconds = -1;

    public HazelcastCache() {
    }

    public HazelcastCache(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public HazelcastCache(IHazelcastClientFactory hazelcastClientFactory, String name) {
        super(name);
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public HazelcastCache(IHazelcastClientFactory hazelcastClientFactory, String name, long capacity) {
        super(name, capacity);
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public HazelcastCache(IHazelcastClientFactory hazelcastClientFactory, String name,
            long capacity, long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        long expireAfterWrite = getExpireAfterWrite();
        long expireAfterAccess = getExpireAfterAccess();
        if (expireAfterAccess > 0 || expireAfterWrite > 0) {
            timeToLiveSeconds = expireAfterAccess > 0 ? expireAfterAccess : expireAfterWrite;
        } else {
            timeToLiveSeconds = -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // EMPTY
    }

    protected IMap<String, Object> getHazelcastMap() {
        HazelcastClient client = getHazelcastClientFactory().getHazelcastClient();
        if (client != null) {
            return client.getMap(getName());
        }
        return null;
    }

    protected void dispostHazelcastMap() {
        getHazelcastClientFactory().returnHazelcastClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            try {
                return map.size();
            } finally {
                dispostHazelcastMap();
            }
        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        if (entry instanceof CacheEntry) {
            CacheEntry ce = (CacheEntry) entry;
            set(key, ce, ce.getExpireAfterWrite(), ce.getExpireAfterAccess());
        } else {
            set(key, entry, timeToLiveSeconds, timeToLiveSeconds);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            long ttl = expireAfterAccess > 0 ? expireAfterAccess
                    : (expireAfterWrite > 0 ? expireAfterWrite : timeToLiveSeconds);
            try {
                if (ttl > 0) {
                    map.set(key, entry, ttl, TimeUnit.SECONDS);
                } else {
                    map.put(key, entry);
                    // map.putAsync(key, entry);
                    // map.tryPut(key, entry, 5000, TimeUnit.MILLISECONDS);
                }
            } finally {
                dispostHazelcastMap();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            try {
                return map.get(key);
            } finally {
                dispostHazelcastMap();
            }
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            try {
                // map.removeAsync(key);
                map.remove(key);
            } finally {
                dispostHazelcastMap();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            try {
                map.clear();
            } finally {
                dispostHazelcastMap();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        IMap<String, Object> map = getHazelcastMap();
        if (map != null) {
            try {
                return map.get(key) != null;
            } finally {
                dispostHazelcastMap();
            }
        } else {
            return false;
        }
    }
}
