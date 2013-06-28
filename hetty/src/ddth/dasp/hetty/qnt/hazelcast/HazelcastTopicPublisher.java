package ddth.dasp.hetty.qnt.hazelcast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.PoolConfig;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public class HazelcastTopicPublisher implements ITopicPublisher {

    private IHazelcastClientFactory hazelcastClientFactory;
    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;
    private PoolConfig poolConfig;
    private Set<IHazelcastClient> allocatedHazelcastClients = new HashSet<IHazelcastClient>();

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public HazelcastTopicPublisher setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public HazelcastTopicPublisher setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public HazelcastTopicPublisher setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public HazelcastTopicPublisher setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    private IHazelcastClient getHazelcastClient() {
        IHazelcastClient hazelcastClient = hazelcastClientFactory.getHazelcastClient(
                hazelcastServers, hazelcastUsername, hazelcastPassword, poolConfig);
        if (hazelcastClient != null) {
            allocatedHazelcastClients.add(hazelcastClient);
        }
        return hazelcastClient;
    }

    private void returnHazelcastClient(IHazelcastClient hazelcastClient) {
        if (hazelcastClient != null) {
            try {
                allocatedHazelcastClients.remove(hazelcastClient);
            } finally {
                hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
            }
        }
    }

    public void init() {
    }

    public void destroy() {
        for (IHazelcastClient hazelcastClient : allocatedHazelcastClients) {
            try {
                hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
            } catch (Exception e) {
            }
        }
        allocatedHazelcastClients.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(String topicName, Object obj) {
        return publish(topicName, obj, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(String topicName, Object obj, long timeout, TimeUnit timeUnit) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                if (obj instanceof IResponse) {
                    IResponse response = (IResponse) obj;
                    hazelcastClient.publish(topicName, response.serialize());
                } else {
                    hazelcastClient.publish(topicName, obj);
                }
                return true;
            } finally {
                returnHazelcastClient(hazelcastClient);
            }
        }
        return false;
    }
}
