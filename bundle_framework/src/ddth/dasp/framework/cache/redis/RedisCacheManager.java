package ddth.dasp.framework.cache.redis;

import org.osgi.framework.BundleContext;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.impl.jedis.RedisClientFactory;
import ddth.dasp.common.utils.OsgiUtils;
import ddth.dasp.framework.cache.AbstractCacheManager;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://www.hazelcast.com/">Hazelcast</a> implementation of
 * {@link ICacheManager}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class RedisCacheManager extends AbstractCacheManager {

    private boolean myOwnRedisClientFactory = false;
    private IRedisClientFactory redisClientFactory;
    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;

    public IRedisClientFactory getRedisClientFactory() {
        if (redisClientFactory != null) {
            return redisClientFactory;
        }
        /*
         * If the Redis client factory has not been set, try to get it from OSGi
         * container.
         */
        BundleContext bundleContext = getBundleContext();
        if (bundleContext != null) {
            IRedisClientFactory _redisClientFactory = OsgiUtils.getService(bundleContext,
                    IRedisClientFactory.class);
            if (_redisClientFactory != null) {
                return _redisClientFactory;
            }
        }
        /*
         * Build an instance of Redis client factory for my own.
         */
        synchronized (this) {
            // safeguard check
            if (redisClientFactory == null) {
                RedisClientFactory hzcf = new RedisClientFactory();
                hzcf.init();
                redisClientFactory = hzcf;
                myOwnRedisClientFactory = true;
            }
        }
        return redisClientFactory;
    }

    public RedisCacheManager setRedisClientFactory(IRedisClientFactory redisClientFactory) {
        this.redisClientFactory = redisClientFactory;
        return this;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public RedisCacheManager setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public RedisCacheManager setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisCacheManager setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public RedisCacheManager setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        if (redisClientFactory == null) {
            RedisClientFactory rcf = new RedisClientFactory();
            rcf.init();
            redisClientFactory = rcf;
            myOwnRedisClientFactory = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (myOwnRedisClientFactory) {
                ((RedisClientFactory) redisClientFactory).destroy();
            }
        } finally {
            super.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RedisCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        RedisCache cache = new RedisCache(redisClientFactory, name);
        cache.setCapacity(capacity > 0 ? capacity : getDefaultCacheCapacity());
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setExpireAfterWrite(expireAfterWrite);

        cache.setRedisHost(redisHost).setRedisPort(redisPort).setRedisUsername(redisUsername)
                .setRedisPassword(redisPassword);

        cache.init();
        return cache;
    }
}
