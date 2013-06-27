package ddth.dasp.test.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.impl.AbstractMessageListener;
import ddth.dasp.common.hazelcastex.impl.HazelcastClientFactory;

public class TestHazelcastClient {

    private static class MyMessageListener extends AbstractMessageListener<String> {

        private int counter = 0;

        public MyMessageListener(String topicName, IHazelcastClient hazelcastClient) {
            super(topicName, hazelcastClient);
        }

        @Override
        public void onMessage(String message) {
            counter++;
            System.out.println("Received: " + "[" + counter + "/" + message + "/"
                    + message.getClass() + "]");
            if (counter > 3) {
                unsubscribe();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final String TOPIC = "_TOPIC_";
        final String hazelcastUsername = "DzitAppServer";
        final String hazelcastPassword = "h2z3lc2st";
        final List<String> hazelcastServers = new ArrayList<String>();
        hazelcastServers.add("localhost:8700");

        final IHazelcastClientFactory hazelcastClientFactory = new HazelcastClientFactory();
        hazelcastClientFactory.init();

        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        IHazelcastClient hazelcastClient = hazelcastClientFactory
                                .getHazelcastClient(hazelcastServers, hazelcastUsername,
                                        hazelcastPassword);
                        try {
                            System.out.println(hazelcastClient + ": " + hazelcastClient.ping()
                                    + ":"
                                    + hazelcastClient.queuePoll("_QUEUE_", 3, TimeUnit.SECONDS));
                        } finally {
                            hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
                        }
                    }
                }
            };
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        // {
        // for (int i = 0; i < 100; i++) {
        // IHazelcastClient hazelcastClient =
        // hazelcastClientFactory.getHazelcastClient(
        // hazelcastServers, hazelcastUsername, hazelcastPassword);
        // System.out.println(hazelcastClient + ": " + hazelcastClient.ping() +
        // ":"
        // + hazelcastClient.queuePoll("_QUEUE_"));
        // hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
        // }
        // }

        System.exit(0);

        IHazelcastClient hazelcastClient = hazelcastClientFactory.getHazelcastClient(
                hazelcastServers, hazelcastUsername, hazelcastPassword);
        hazelcastClient.subscribe(TOPIC, new MyMessageListener(TOPIC, hazelcastClient));

        for (int i = 0; i < 10; i++) {
            Object value = i % 2 == 0 ? String.valueOf(i) : i;
            hazelcastClient.publish(TOPIC, value);
            System.out.println("Published: " + value);
            Thread.sleep(1000);
        }

        hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
        hazelcastClientFactory.destroy();
    }
}
