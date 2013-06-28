package ddth.dasp.common.redis.impl.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.redis.PoolConfig;
import ddth.dasp.common.redis.impl.AbstractRedisClientFactory;
import ddth.dasp.common.redis.impl.RedisClientPool;

public class RedisClientFactory extends AbstractRedisClientFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisClientFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected RedisClientPool buildRedisClientPool(String host, int port, String username,
            String password, PoolConfig poolConfig) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building a Redis client pool {host:" + host + ";port:" + port
                    + ";username:" + username + "}...");
        }
        RedisClientPoolableObjectFactory factory = new RedisClientPoolableObjectFactory(host, port,
                username, password);
        RedisClientPool redisClientPool = new RedisClientPool(factory, poolConfig);
        redisClientPool.init();
        return redisClientPool;
    }
}
