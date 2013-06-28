package ddth.dasp.test.redis;

import java.io.IOException;
import java.util.Random;

import ddth.dasp.common.redis.IRedisClient;
import ddth.dasp.common.redis.IRedisClientFactory;
import ddth.dasp.common.redis.impl.jedis.RedisClientFactory;

public class TestRedisClient2 {

    private static Random r = new Random(System.currentTimeMillis());

    private static byte[] randomInput() {
        int length = Math.abs(r.nextInt() % 1024);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) r.nextInt();
        }
        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final String QUEUE = "queue-1";

        final IRedisClientFactory redisClientFactory = new RedisClientFactory();
        System.out.println(redisClientFactory);

        final int NUM_THREADS = 16;
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        IRedisClient redisClient = redisClientFactory.getRedisClient("localhost",
                                IRedisClient.DEFAULT_REDIS_PORT);
                        try {
                            byte[] data = redisClient.listPopAsBinary(QUEUE);
                            // if (data == null) {
                            // try {
                            // Thread.sleep(1);
                            // } catch (InterruptedException e) {
                            // e.printStackTrace();
                            // }
                            // Thread.yield();
                            // }
                        } finally {
                            redisClientFactory.returnRedisClient(redisClient);
                        }
                    }
                }
            };
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].start();
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
        }

        // for (int i = 0; i < 1000; i++) {
        // byte[] input = randomInput();
        // // redisClient.listPush(QUEUE, input);
        // byte[] output = redisClient.listPopAsBinary(QUEUE);
        // if (!Arrays.equals(input, output)) {
        // System.out.print("NOT OK!");
        // }
        // }
    }
}
