package ddth.dasp.common.redis;

public interface IMessageListener {
    public void onMessage(String channel, byte[] message);

    public void unsubscribe(String channel);
}
