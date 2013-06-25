package ddth.dasp.common.hazelcastex.impl;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IMessageListener;

public abstract class AbstractMessageListener<E> implements IMessageListener<E> {

    private IHazelcastClient hazelcastClient;
    private String topicName;

    public AbstractMessageListener(String topicName, IHazelcastClient hazelcastClient) {
        this.hazelcastClient = hazelcastClient;
        this.topicName = topicName;
    }

    protected IHazelcastClient getHazelcastClient() {
        return hazelcastClient;
    }

    protected String getTopicName() {
        return topicName;
    }

    public void unsubscribe() {
        hazelcastClient.unsubscribe(topicName, this);
    }
}
