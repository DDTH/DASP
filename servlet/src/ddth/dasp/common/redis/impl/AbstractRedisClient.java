package ddth.dasp.common.redis.impl;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientPool;

public abstract class AbstractRedisClient implements IRedisClient {

    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = 6379;
    private IRedisClientPool redisClientPool;

    protected IRedisClientPool getRedisClientPool() {
        return redisClientPool;
    }

    public AbstractRedisClient setRedisClientPool(IRedisClientPool redisClientPool) {
        this.redisClientPool = redisClientPool;
        return this;
    }

    protected String getRedisHost() {
        return redisHost;
    }

    public AbstractRedisClient setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected int getRedisPort() {
        return redisPort;
    }

    public AbstractRedisClient setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public AbstractRedisClient setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public AbstractRedisClient setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }
}
