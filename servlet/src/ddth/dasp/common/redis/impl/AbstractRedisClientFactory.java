package ddth.dasp.common.redis.impl;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.PoolConfig;

public abstract class AbstractRedisClientFactory implements IRedisClientFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRedisClientFactory.class);

    /**
     * Stores established Redis client pools as a map of {key:pool}.
     */
    private ConcurrentMap<String, RedisClientPool> redisClientPools = new ConcurrentHashMap<String, RedisClientPool>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        for (Entry<String, RedisClientPool> entry : redisClientPools.entrySet()) {
            RedisClientPool clientPool = entry.getValue();
            try {
                clientPool.close();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }

    protected static String calcRedisPoolName(String host, int port, String username,
            String password, PoolConfig poolConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(host != null ? host : "NULL");
        sb.append(".");
        sb.append(port);
        sb.append(".");
        sb.append(username != null ? username : "NULL");
        sb.append(".");
        int passwordHashcode = password != null ? password.hashCode() : "NULL".hashCode();
        int poolHashcode = poolConfig != null ? poolConfig.hashCode() : "NULL".hashCode();
        return sb.append(passwordHashcode).append(".").append(poolHashcode).toString();
    }

    protected RedisClientPool getPool(String poolName) {
        return redisClientPools.get(poolName);
    }

    protected RedisClientPool buildRedisClientPool(String host, int port, String username,
            String password) {
        return buildRedisClientPool(host, port, username, password, null);
    }

    protected abstract RedisClientPool buildRedisClientPool(String host, int port, String username,
            String password, PoolConfig poolConfig);

    /**
     * {@inheritDoc}
     */
    @Override
    public IRedisClient getRedisClient(String host) {
        return getRedisClient(host, IRedisClient.DEFAULT_REDIS_PORT, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRedisClient getRedisClient(String host, int port) {
        return getRedisClient(host, port, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRedisClient getRedisClient(String host, int port, String username, String password) {
        return getRedisClient(host, port, username, password, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRedisClient getRedisClient(String host, int port, String username, String password,
            PoolConfig poolConfig) {
        String poolName = calcRedisPoolName(host, port, username, password, poolConfig);

        RedisClientPool redisClientPool = null;
        synchronized (redisClientPools) {
            redisClientPool = getPool(poolName);
            if (redisClientPool == null) {
                try {
                    redisClientPool = buildRedisClientPool(host, port, username, password,
                            poolConfig);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                redisClientPools.put(poolName, redisClientPool);
            }
        }
        try {
            return redisClientPool != null ? redisClientPool.borrowObject() : null;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void returnRedisClient(IRedisClient redisClient) {
        if (redisClient != null) {
            redisClient.close();
        }
    }
}
