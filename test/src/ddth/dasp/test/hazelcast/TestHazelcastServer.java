package ddth.dasp.test.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class TestHazelcastServer {
    public static void main(String[] args) {
        Config cfg = new Config();
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(cfg);
        System.out.println(hz);
    }
}
