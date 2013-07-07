package ddth.dasp.hetty.qnt.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.PoolConfig;
import ddth.dasp.hetty.qnt.IQueueReader;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class RedisQueue implements IQueueReader, IQueueWriter {

    private final Logger LOGGER = LoggerFactory.getLogger(RedisQueue.class);
    private final static long QUEUE_TTL_SECONDS = 60;

    private IRedisClientFactory redisClientFactory;
    private String redisHost = "localhost";
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private String redisUsername, redisPassword;
    private PoolConfig poolConfig;
    private int queueSizeThreshold = 1000;
    private long lastWrite = System.currentTimeMillis();
    private Set<IRedisClient> allocatedRedisClients = new HashSet<IRedisClient>();

    protected IRedisClientFactory getRedisClientFactory() {
        return redisClientFactory;
    }

    public RedisQueue setRedisClientFactory(IRedisClientFactory redisClientFactory) {
        this.redisClientFactory = redisClientFactory;
        return this;
    }

    protected String getRedisHost() {
        return redisHost;
    }

    public RedisQueue setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected int getRedisPort() {
        return redisPort;
    }

    public RedisQueue setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public RedisQueue setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisQueue setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisQueue setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    protected int getQueueSizeThreshold() {
        return queueSizeThreshold;
    }

    public RedisQueue setQueueSizeThreshold(int queueSizeThreshold) {
        this.queueSizeThreshold = queueSizeThreshold;
        return this;
    }

    private IRedisClient getRedisClient() {
        IRedisClient redisClient = redisClientFactory.getRedisClient(redisHost, redisPort,
                redisUsername, redisPassword, poolConfig);
        if (redisClient != null) {
            allocatedRedisClients.add(redisClient);
        }
        return redisClient;
    }

    private void returnRedisClient(IRedisClient redisClient) {
        if (redisClient != null) {
            try {
                allocatedRedisClients.remove(redisClient);
            } finally {
                redisClientFactory.returnRedisClient(redisClient);
            }
        }
    }

    public void init() {
    }

    public void destroy() {
        for (IRedisClient redisClient : allocatedRedisClients) {
            try {
                redisClientFactory.returnRedisClient(redisClient);
            } catch (Exception e) {
            }
        }
        allocatedRedisClients.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueWrite(String queueName, Object value) {
        return queueWrite(queueName, value, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueWrite(String queueName, Object value, long timeout, TimeUnit timeUnit) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                int queueSize = (int) redisClient.listSize(queueName);
                if (queueSize < 0 || queueSize > queueSizeThreshold) {
                    LOGGER.warn("Queue not available or full!");
                    return false;
                }
                if (value instanceof byte[]) {
                    redisClient.listPush(queueName, (byte[]) value);
                } else {
                    redisClient.listPush(queueName, value != null ? value.toString() : null);
                }
                if (System.currentTimeMillis() - lastWrite > QUEUE_TTL_SECONDS * 1000) {
                    lastWrite = System.currentTimeMillis();
                    redisClient.expire(queueName, (int) QUEUE_TTL_SECONDS);
                }
                return true;
            } finally {
                returnRedisClient(redisClient);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object queueRead(String queueName) {
        return queueRead(queueName, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object queueRead(String queueName, long timeout, TimeUnit timeUnit) {
        IRedisClient redisClient = getRedisClient();
        try {
            return redisClient != null ? redisClient.listPopAsBinary(queueName, true,
                    (int) timeUnit.toSeconds(timeout)) : null;
        } finally {
            returnRedisClient(redisClient);
        }
    }
}
