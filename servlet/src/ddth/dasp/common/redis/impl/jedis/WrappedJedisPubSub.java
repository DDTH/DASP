package ddth.dasp.common.redis.impl.jedis;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;
import ddth.dasp.common.redis.IMessageListener;

public class WrappedJedisPubSub extends BinaryJedisPubSub {

    private IMessageListener messageListener;
    private String channelName;

    public WrappedJedisPubSub(String channelName, IMessageListener messageListener) {
        this.channelName = channelName;
        this.messageListener = messageListener;
    }

    public String getChannelName() {
        return channelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(byte[] channel, byte[] message) {
        String channelName = SafeEncoder.encode(channel);
        messageListener.onMessage(channelName, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
        // System.out.println("onPMessage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {
        // System.out.println("onPSubscribe");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
        // System.out.println("onPUnsubscribe");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
        // System.out.println("onSubscribe");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        // System.out.println("onUnsubscribe");
    }
}
