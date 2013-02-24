package ddth.dasp.hetty.qnt.hazelcast;

import java.util.concurrent.TimeUnit;

import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public class HazelcastTopicPublisher implements ITopicPublisher {

    private IHazelcastClientFactory hazelcastClientFactory;
    private String hazelcastTopicName;

    protected String getHazelcastTopicName() {
        return hazelcastTopicName;
    }

    public void setHazelcastTopicName(String hazelcastTopicName) {
        this.hazelcastTopicName = hazelcastTopicName;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public void init() {
    }

    public void destroy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publishToTopic(Object obj) {
        return publishToTopic(obj, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publishToTopic(Object obj, long timeout, TimeUnit timeUnit) {
        if (obj instanceof IResponse) {
            IResponse response = (IResponse) obj;
            hazelcastClientFactory.publishToTopic(hazelcastTopicName, response.serialize());
        } else {
            hazelcastClientFactory.publishToTopic(hazelcastTopicName, obj);
        }
        return true;
    }
}
