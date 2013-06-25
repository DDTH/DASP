package ddth.dasp.test.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import ddth.dasp.common.hazelcast.HazelcastClientFactory;

public class TestHazelcastTopic implements MessageListener<Object>, Runnable {

    private static HazelcastClientFactory hzcf;
    private final static String TOPIC_NAME = "TOPIC";

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> hzservers = new ArrayList<String>();
        hzservers.add("127.0.0.21:5701");

        hzcf = new HazelcastClientFactory();
        hzcf.setHazelcastServers(hzservers);
        hzcf.init();

        TestHazelcastTopic test = new TestHazelcastTopic();
        hzcf.subcribeToTopic(TOPIC_NAME, test);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
        scheduler.scheduleAtFixedRate(test, 10, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(Message<Object> msg) {
        System.out.println("Receive: " + msg.getMessageObject());
    }

    @Override
    public void run() {
        try {
            if (new Random(System.currentTimeMillis()).nextBoolean()) {
                Object obj = System.currentTimeMillis();
                System.out.println("Publish: " + obj);
                hzcf.publishToTopic(TOPIC_NAME, obj);
            }
        } catch (Exception e) {
            // EMPTY
        }
    }
}
