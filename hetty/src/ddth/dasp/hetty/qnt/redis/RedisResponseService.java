package ddth.dasp.hetty.qnt.redis;

import java.util.concurrent.TimeUnit;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.redis.IMessageListener;
import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.PoolConfig;
import ddth.dasp.hetty.front.AbstractHettyResponseService;
import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.message.IResponse;

/*
 * TODO: what would happen if a message comes when onMessage() is busy?
 * @see http://davidmarquis.wordpress.com/2013/01/03/reliable-delivery-message-queues-with-redis/
 */
public class RedisResponseService extends AbstractHettyResponseService implements IMessageListener {

    private IRedisClientFactory redisClientFactory;
    private String redisHost = "localhost";
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;
    private String redisUsername, redisPassword;
    private PoolConfig poolConfig;
    private IMessageFactory messageFactory;
    private String topicName;
    private boolean _destroyed = false, _listening = false;

    protected String getTopicName() {
        return topicName;
    }

    public RedisResponseService setTopicName(String topicName) {
        this.topicName = topicName;
        return this;
    }

    protected IRedisClientFactory getRedisClientFactory() {
        return redisClientFactory;
    }

    public RedisResponseService setRedisClientFactory(IRedisClientFactory redisClientFactory) {
        this.redisClientFactory = redisClientFactory;
        return this;
    }

    protected String getRedisHost() {
        return redisHost;
    }

    public RedisResponseService setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected int getRedisPort() {
        return redisPort;
    }

    public RedisResponseService setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public RedisResponseService setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public RedisResponseService setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public RedisResponseService setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    protected IMessageFactory getMessageFactory() {
        return messageFactory;
    }

    public RedisResponseService setMessageFactory(IMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    private class MessageListenerKeeper implements Runnable {
        private IMessageListener messageListener;

        public MessageListenerKeeper(IMessageListener messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public void run() {
            if (!_destroyed) {
                try {
                    if (!_listening) {
                        IRedisClient redisClient = getRedisClient();
                        if (redisClient != null) {
                            _listening = true;
                            redisClient.subscribe(topicName, messageListener);
                        }
                    }
                } finally {
                    DaspGlobal.getScheduler().schedule(this, 5000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public void init() {
        Runnable command = new MessageListenerKeeper(this);
        DaspGlobal.getScheduler().schedule(command, 2000, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        _destroyed = true;
        try {
            unsubscribe(topicName);
        } finally {
            returnRedisClient();
        }
    }

    private IRedisClient _redisClient;

    synchronized private IRedisClient getRedisClient() {
        if (_redisClient == null) {
            _redisClient = redisClientFactory.getRedisClient(redisHost, redisPort, redisUsername,
                    redisPassword, poolConfig);
        }
        return _redisClient;
    }

    synchronized private void returnRedisClient() {
        try {
            redisClientFactory.returnRedisClient(_redisClient);
        } finally {
            _redisClient = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(String channel) {
        try {
            if (_redisClient != null) {
                _redisClient.unsubscribe(channel, this);
            }
        } finally {
            _listening = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(String channel, byte[] message) {
        IResponse response = messageFactory.deserializeResponse((byte[]) message);
        writeResponse(response);
    }
}
