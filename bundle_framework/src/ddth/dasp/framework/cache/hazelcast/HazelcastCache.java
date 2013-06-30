package ddth.dasp.framework.cache.hazelcast;

import java.util.List;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.PoolConfig;
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

    private HazelcastCacheManager hazelcastCacheManager;
    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;
    private PoolConfig poolConfig;
    private long timeToLiveSeconds = -1;

    public HazelcastCache(HazelcastCacheManager hazelcastCacheManager) {
        this.hazelcastCacheManager = hazelcastCacheManager;
    }

    public HazelcastCache(HazelcastCacheManager hazelcastCacheManager, String name) {
        super(name);
        this.hazelcastCacheManager = hazelcastCacheManager;
    }

    public HazelcastCache(HazelcastCacheManager hazelcastCacheManager, String name, long capacity) {
        super(name, capacity);
        this.hazelcastCacheManager = hazelcastCacheManager;
    }

    public HazelcastCache(HazelcastCacheManager hazelcastCacheManager, String name, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
        this.hazelcastCacheManager = hazelcastCacheManager;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastCacheManager.getHazelcastClientFactory();
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public HazelcastCache setHazelcastServer(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public HazelcastCache setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public HazelcastCache setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public HazelcastCache setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
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

    private IHazelcastClient getHazelcastClient() {
        IHazelcastClientFactory hazelcastClientFactory = getHazelcastClientFactory();
        return hazelcastClientFactory != null ? hazelcastClientFactory.getHazelcastClient(
                hazelcastServers, hazelcastUsername, hazelcastPassword, poolConfig) : null;
    }

    private void returnHazelcastClient(IHazelcastClient hazelcastClient) {
        if (hazelcastClient != null) {
            getHazelcastClientFactory().returnHazelcastClient(hazelcastClient);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        try {
            return hazelcastClient != null ? hazelcastClient.mapSize(getName()) : -1;
        } finally {
            returnHazelcastClient(hazelcastClient);
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
        IHazelcastClient hazelcastClient = getHazelcastClient();
        if (hazelcastClient != null) {
            long ttl = expireAfterAccess > 0 ? expireAfterAccess
                    : (expireAfterWrite > 0 ? expireAfterWrite : timeToLiveSeconds);
            try {
                hazelcastClient.mapSet(getName(), key, entry, (int) ttl);
            } finally {
                returnHazelcastClient(hazelcastClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        try {
            return hazelcastClient != null ? hazelcastClient.mapGet(getName(), key) : null;
        } finally {
            returnHazelcastClient(hazelcastClient);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                hazelcastClient.mapDelete(getName(), key);
            } finally {
                returnHazelcastClient(hazelcastClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                hazelcastClient.mapDeleteAll(getName());
            } finally {
                returnHazelcastClient(hazelcastClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        try {
            return hazelcastClient != null ? hazelcastClient.mapGet(getName(), key) != null : false;
        } finally {
            returnHazelcastClient(hazelcastClient);
        }
    }
}
