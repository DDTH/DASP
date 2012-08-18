package ddth.dasp.framework.cache.hazelcast;

import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.IMap;

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

    private HazelcastClient hazelcastClient;
    private IMap<String, Object> hazelcastMap;

    public HazelcastCache(HazelcastClient hazelcastClient) {
        this.hazelcastClient = hazelcastClient;
    }

    public HazelcastCache(HazelcastClient hazelcastClient, String name) {
        super(name);
        this.hazelcastClient = hazelcastClient;
    }

    public HazelcastCache(HazelcastClient hazelcastClient, String name, long capacity) {
        super(name, capacity);
        this.hazelcastClient = hazelcastClient;
    }

    public HazelcastCache(HazelcastClient hazelcastClient, String name, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
        this.hazelcastClient = hazelcastClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        hazelcastMap = hazelcastClient.getMap(getName());
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
    public long getSize() {
        return hazelcastMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, Object entry) {
        long expireAfterWrite = getExpireAfterWrite();
        long expireAfterAccess = getExpireAfterAccess();
        if (expireAfterWrite >= 0 && expireAfterAccess >= 0) {
            hazelcastMap.put(key, entry, expireAfterWrite >= 0 ? expireAfterWrite
                    : expireAfterAccess, TimeUnit.SECONDS);
        } else {
            hazelcastMap.putAsync(key, entry);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        return hazelcastMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        hazelcastMap.removeAsync(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        hazelcastMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        return hazelcastMap.get(key) != null;
    }
}
