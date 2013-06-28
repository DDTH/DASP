package ddth.dasp.test.redis;

import redis.clients.jedis.Jedis;
import ddth.dasp.common.redis.IMessageListener;
import ddth.dasp.common.redis.impl.jedis.WrappedJedisPubSub;

public class TestJedisClient {

    public static void main(String[] args) throws InterruptedException {
        final Jedis jedis = new Jedis("localhost");
        jedis.subscribe(new WrappedJedisPubSub("channel-1", new IMessageListener() {
            @Override
            public void onMessage(String channel, byte[] message) {
                System.out.println(channel + ":" + new String(message));
            }

            @Override
            public void unsubscribe(String channel) {
                // EMPTY
            }
        }), "channel-1".getBytes());
    }
}
