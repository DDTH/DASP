package ddth.dasp.common.redis.impl;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientPool;
import ddth.dasp.common.redis.PoolConfig;

public class RedisClientPool extends GenericObjectPool<AbstractRedisClient> implements
        IRedisClientPool {

    private Logger LOGGER = LoggerFactory.getLogger(RedisClientPool.class);
    private PoolConfig poolConfig;

    public RedisClientPool(PoolableObjectFactory<AbstractRedisClient> factory, PoolConfig poolConfig) {
        super(factory);
        setPoolConfig(poolConfig);
    }

    public RedisClientPool setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        if (poolConfig != null) {
            int maxActive = poolConfig != null ? poolConfig.getMaxActive()
                    : PoolConfig.DEFAULT_MAX_ACTIVE;
            long maxWaitTime = poolConfig != null ? poolConfig.getMaxWaitTime()
                    : PoolConfig.DEFAULT_MAX_WAIT_TIME;
            int maxIdle = poolConfig != null ? poolConfig.getMaxIdle()
                    : PoolConfig.DEFAULT_MAX_IDLE;
            int minIdle = poolConfig != null ? poolConfig.getMinIdle()
                    : PoolConfig.DEFAULT_MIN_IDLE;
            LOGGER.debug("Updating Redis client pool {maxActive:" + maxActive + ";maxWait:"
                    + maxWaitTime + ";minIdle:" + minIdle + ";maxIdle:" + maxIdle + "}...");
            this.setMaxActive(maxActive);
            this.setMaxIdle(maxIdle);
            this.setMaxWait(maxWaitTime);
            this.setMinIdle(minIdle);
        }
        return this;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRedisClient borrowObject() throws Exception {
        AbstractRedisClient redisClient = super.borrowObject();
        redisClient.setRedisClientPool(this);
        return redisClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRedisClient borrowRedisClient() {
        try {
            return borrowObject();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void returnRedisClient(IRedisClient redisClient) {
        if (redisClient instanceof AbstractRedisClient)
            try {
                returnObject((AbstractRedisClient) redisClient);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
    }
}
