package ddth.dasp.test.redis;

import java.io.IOException;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.impl.AbstractMessageListener;
import ddth.dasp.common.redis.impl.jedis.RedisClientFactory;

public class TestRedisClient {

    private static class MyMessageListener extends AbstractMessageListener {

        private int counter = 0;

        public MyMessageListener(String channelName, IRedisClient redisClient) {
            super(channelName, redisClient);
        }

        @Override
        public void onMessage(String channel, byte[] message) {
            counter++;
            System.out.println("Received: " + "[" + counter + "/" + channel + "/"
                    + new String(message) + "]");
            if (counter > 3) {
                unsubscribe(channel);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final String CHANNEL = "channel-1";

        IRedisClientFactory redisClientFactory = new RedisClientFactory();
        final IRedisClient redisClient = redisClientFactory.getRedisClient("localhost",
                IRedisClient.DEFAULT_REDIS_PORT, null, null);
        System.out.println(redisClient);

        redisClient.subscribe(CHANNEL, new MyMessageListener(CHANNEL, redisClient));

        System.out.println("END");

        redisClientFactory.returnRedisClient(redisClient);

        // System.out.println(redisClient.ping());

        // Thread t = new Thread() {
        // public void run() {
        // while (true) {
        // Thread.yield();
        // }
        // }
        // };
        // t.start();
    }
}
