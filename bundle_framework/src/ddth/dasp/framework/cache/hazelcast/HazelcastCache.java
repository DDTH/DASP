package ddth.dasp.framework.cache.hazelcast;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.IMap;

import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.framework.cache.AbstractCache;
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
        return getHazelcastClientFactory().getHazelcastClient().getMap(getName());
    }

    protected void dispostHazelcastMap() {
        getHazelcastClientFactory().returnHazelcastClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        try {
            return getHazelcastMap().size();
        } finally {
            dispostHazelcastMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        try {
            if (timeToLiveSeconds > 0) {
                getHazelcastMap().put(key, entry, timeToLiveSeconds, TimeUnit.SECONDS);
            } else {
                getHazelcastMap().putAsync(key, entry);
            }
        } finally {
            dispostHazelcastMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        try {
            return getHazelcastMap().get(key);
        } finally {
            dispostHazelcastMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        try {
            getHazelcastMap().removeAsync(key);
        } finally {
            dispostHazelcastMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        try {
            getHazelcastMap().clear();
        } finally {
            dispostHazelcastMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        try {
            return getHazelcastMap().get(key) != null;
        } finally {
            dispostHazelcastMap();
        }
    }
}
