package ddth.dasp.common.redis.impl;

import ddth.dasp.common.redis.IMessageListener;
import ddth.dasp.common.redis.IRedisClient;

public abstract class AbstractMessageListener implements IMessageListener {

    private IRedisClient redisClient;
    private String channelName;

    public AbstractMessageListener(String channelName, IRedisClient redisClient) {
        this.redisClient = redisClient;
        this.channelName = channelName;
    }

    protected IRedisClient getRedisClient() {
        return redisClient;
    }

    protected String getChannelName() {
        return channelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(String channel) {
        redisClient.unsubscribe(channelName, this);
    }
}
