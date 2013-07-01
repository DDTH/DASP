package ddth.dasp.common.redis.impl;

import java.util.Set;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.util.ConcurrentHashSet;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientPool;
import ddth.dasp.common.redis.PoolConfig;

public class RedisClientPool extends GenericObjectPool<AbstractRedisClient> implements
        IRedisClientPool {

    private Logger LOGGER = LoggerFactory.getLogger(RedisClientPool.class);
    private PoolConfig poolConfig;
    private Set<AbstractRedisClient> activeClients = new ConcurrentHashSet<AbstractRedisClient>();

    public RedisClientPool(PoolableObjectFactory<AbstractRedisClient> factory, PoolConfig poolConfig) {
        super(factory);
        setPoolConfig(poolConfig);
        setTestOnBorrow(true);
        setTestWhileIdle(true);
        setWhenExhaustedAction(WHEN_EXHAUSTED_FAIL);
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        try {
            for (AbstractRedisClient client : activeClients) {
                invalidateObject(client);
            }
        } finally {
            super.close();
        }
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
        // System.out.println("Borrow: " + getNumActive() + "+" + getNumIdle() +
        // "/" + getMaxActive());
        if (redisClient != null) {
            redisClient.setRedisClientPool(this);
            activeClients.add(redisClient);
        }
        return redisClient;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @Override
    public void returnObject(AbstractRedisClient redisClient) throws Exception {
        try {
            super.returnObject(redisClient);
            // super.invalidateObject(redisClient);
            // System.out.println("Return: " + getNumActive() + "+" +
            // getNumIdle() + "/"
            // + getMaxActive());
        } finally {
            activeClients.remove(redisClient);
        }
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
