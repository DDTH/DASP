package ddth.dasp.test.redis;

import java.io.IOException;

import ddth.dasp.common.redis.IMessageListener;
import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.impl.jedis.RedisClientFactory;

public class TestRedisClient {

    public static void main(String[] args) throws IOException {
        IRedisClientFactory redisClientFactory = new RedisClientFactory();
        final IRedisClient redisClient = redisClientFactory.getRedisClient("localhost",
                IRedisClient.DEFAULT_REDIS_PORT, null, null);
        System.out.println(redisClient);

        redisClient.subscribe("channel-1", new IMessageListener() {
            @Override
            public void onMessage(String channel, byte[] message) {
                System.out.println(channel + ":" + new String(message));
            }
        });

        // System.out.println(redisClient.ping());

        Thread t = new Thread() {
            public void run() {
                while (true) {
                    Thread.yield();
                }
            }
        };
        t.start();
    }
}
