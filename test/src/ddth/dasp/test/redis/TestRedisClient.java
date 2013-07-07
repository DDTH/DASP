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
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (counter > 3) {
                unsubscribe(channel);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        IRedisClientFactory redisClientFactory = new RedisClientFactory();
        final IRedisClient redisClient = redisClientFactory.getRedisClient("localhost",
                IRedisClient.DEFAULT_REDIS_PORT, null, null);
        System.out.println(redisClient);

        System.out.println(redisClient.listPopAsBinary("LIST-1", true, 10));

        redisClient.close();
    }
}
