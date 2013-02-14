package ddth.dasp.common.hazelcast;

import com.hazelcast.client.HazelcastClient;

public interface IHazelcastClientFactory {
    public HazelcastClient getHazelcastClient();

    public void returnHazelcastClient();
}
