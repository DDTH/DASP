package ddth.dasp.framework.cache.redis;

import java.io.IOException;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.PoolConfig;
import ddth.dasp.framework.cache.AbstractCache;
import ddth.dasp.framework.cache.CacheEntry;
import ddth.dasp.framework.cache.ICache;
import ddth.dasp.framework.utils.SerializeUtils;

/**
 * <a href="http://www.hazelcast.com/">Hazelcast</a> implementation of
 * {@link ICache}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class RedisCache extends AbstractCache implements ICache {

    private RedisCacheManager redisCacheManager;
    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private PoolConfig poolConfig;
    private long timeToLiveSeconds = -1;

    public RedisCache(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public RedisCache(RedisCacheManager redisCacheManager, String name) {
        super(name);
        this.redisCacheManager = redisCacheManager;
    }

    public RedisCache(RedisCacheManager redisCacheManager, String name, long capacity) {
        super(name, capacity);
        this.redisCacheManager = redisCacheManager;
    }

    public RedisCache(RedisCacheManager redisCacheManager, String name, long capacity,
            long expireAfterWrite, long expireAfterAccess) {
        super(name, capacity, expireAfterWrite, expireAfterAccess);
        this.redisCacheManager = redisCacheManager;
    }

    protected IRedisClientFactory getRedisClientFactory() {
        return redisCacheManager.getRedisClientFactory();
    }

    public String getRedisHost() {
        return redisHost;
    }

    public RedisCache setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    public String getRedisUsername() {
        return redisUsername;
    }

    public RedisCache setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public RedisCache setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public RedisCache setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisCache setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    /**
     * Serializes an object to byte array.
     * 
     * @param obj
     * @return
     * @throws IOException
     */
    protected byte[] serializeObject(Object obj) throws IOException {
        return SerializeUtils.serialize(obj);
    }

    /**
     * Deserializes object from byte array.
     * 
     * @param byteArr
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected Object deserializeObject(byte[] byteArr) throws IOException, ClassNotFoundException {
        return SerializeUtils.deserialize(byteArr);
    }

    /**
     * Generic version of {@link #deserializeObject(byte[])}.
     * 
     * @param byteArr
     * @param clazz
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected <T> T deserializeObject(byte[] byteArr, Class<T> clazz) throws IOException,
            ClassNotFoundException {
        return SerializeUtils.deserialize(byteArr, clazz);
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

    private IRedisClient getRedisClient() {
        IRedisClientFactory redisClientFactory = getRedisClientFactory();
        return redisClientFactory != null ? redisClientFactory.getRedisClient(redisHost, redisPort,
                redisUsername, redisPassword, poolConfig) : null;
    }

    private void returnRedisClient(IRedisClient redisClient) {
        if (redisClient != null) {
            getRedisClientFactory().returnRedisClient(redisClient);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        IRedisClient redisClient = getRedisClient();
        try {
            return redisClient != null ? redisClient.hashSize(getName()) : -1;
        } finally {
            returnRedisClient(redisClient);
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
            set(key, entry, getExpireAfterWrite(), getExpireAfterAccess());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String key, Object entry, long expireAfterWrite, long expireAfterAccess) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                long ttl = expireAfterAccess > 0 ? expireAfterAccess
                        : (expireAfterWrite > 0 ? expireAfterWrite : timeToLiveSeconds);
                redisClient.hashSet(getName(), key, serializeObject(entry), (int) ttl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalGet(String key) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                byte[] obj = redisClient.hashGetAsBinary(getName(), key);
                if (obj != null) {
                    long expireAfterAccess = getExpireAfterAccess();
                    if (expireAfterAccess > 0) {
                        redisClient.expire(getName(), (int) expireAfterAccess);
                    }
                    return deserializeObject(obj);
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                returnRedisClient(redisClient);
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
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                redisClient.hashDelete(getName(), key);
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                redisClient.delete(getName());
            } finally {
                returnRedisClient(redisClient);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String key) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                return redisClient.hashGet(getName(), key) != null;
            } finally {
                returnRedisClient(redisClient);
            }
        }
        return false;
    }
}
