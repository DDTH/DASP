package ddth.dasp.common.redis.impl.jedis;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.impl.AbstractRedisClient;

public class RedisClientPoolableObjectFactory extends
        BasePoolableObjectFactory<AbstractRedisClient> implements
        PoolableObjectFactory<AbstractRedisClient> {

    private String redisHost, redisUser, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;

    public RedisClientPoolableObjectFactory(String host, int port, String username, String password) {
        redisHost = host;
        redisPort = port;
        redisUser = username;
        redisPassword = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateObject(AbstractRedisClient redisClient) throws Exception {
        redisClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyObject(AbstractRedisClient redisClient) throws Exception {
        redisClient.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractRedisClient makeObject() throws Exception {
        AbstractRedisClient redisClient = new PoolableRedisClient();
        redisClient.setRedisHost(redisHost).setRedisPort(redisPort).setRedisUsername(redisUser)
                .setRedisPassword(redisPassword);
        redisClient.init();
        return redisClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivateObject(AbstractRedisClient redisClient) throws Exception {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateObject(AbstractRedisClient redisClient) {
        try {
            return redisClient.ping() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
