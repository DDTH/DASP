package ddth.dasp.hetty.qnt.hazelcast;

import java.util.List;
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

    private IHazelcastClient _hazelcastClient;

    synchronized protected IHazelcastClient getHazelcastClient() {
        if (_hazelcastClient == null) {
            _hazelcastClient = hazelcastClientFactory.getHazelcastClient(hazelcastServers,
                    hazelcastUsername, hazelcastPassword, poolConfig);
        }
        return _hazelcastClient;
    }

    synchronized protected void returnHazelcastClient() {
        try {
            hazelcastClientFactory.returnHazelcastClient(_hazelcastClient);
        } finally {
            _hazelcastClient = null;
        }
    }

    public void init() {
    }

    public void destroy() {
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
                returnHazelcastClient();
            }
        }
        return false;
    }
}
