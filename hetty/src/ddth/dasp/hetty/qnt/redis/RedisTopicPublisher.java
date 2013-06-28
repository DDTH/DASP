package ddth.dasp.hetty.qnt.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.PoolConfig;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public class RedisTopicPublisher implements ITopicPublisher {

    private IRedisClientFactory redisClientFactory;
    private String redisHost = "localhost";
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private String redisUsername, redisPassword;
    private PoolConfig poolConfig;
    private Set<IRedisClient> allocatedRedisClients = new HashSet<IRedisClient>();

    protected IRedisClientFactory getRedisClientFactory() {
        return redisClientFactory;
    }

    public RedisTopicPublisher setRedisClientFactory(IRedisClientFactory redisClientFactory) {
        this.redisClientFactory = redisClientFactory;
        return this;
    }

    protected String getRedisHost() {
        return redisHost;
    }

    public RedisTopicPublisher setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected int getRedisPort() {
        return redisPort;
    }

    public RedisTopicPublisher setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public RedisTopicPublisher setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisTopicPublisher setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisTopicPublisher setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
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
    public boolean publish(String topicName, Object obj) {
        return publish(topicName, obj, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(String topicName, Object obj, long timeout, TimeUnit timeUnit) {
        IRedisClient redisClient = getRedisClient();
        if (redisClient != null) {
            try {
                if (obj instanceof IResponse) {
                    IResponse response = (IResponse) obj;
                    redisClient.publish(topicName, response.serialize());
                } else if (obj instanceof byte[]) {
                    redisClient.publish(topicName, (byte[]) obj);
                } else {
                    redisClient.publish(topicName, obj != null ? obj.toString() : null);
                }
                return true;
            } finally {
                returnRedisClient(redisClient);
            }
        }
        return false;
    }
}
