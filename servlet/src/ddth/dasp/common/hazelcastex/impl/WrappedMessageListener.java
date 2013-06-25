package ddth.dasp.common.hazelcastex.impl;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import ddth.dasp.common.hazelcastex.IMessageListener;

public class WrappedMessageListener<E> implements MessageListener<E> {

    private IMessageListener<E> messageListener;
    private String topicName;

    public WrappedMessageListener(String topicName, IMessageListener<E> messageListener) {
        this.topicName = topicName;
        this.messageListener = messageListener;
    }

    public String getTopicName() {
        return topicName;
    }

    @Override
    public void onMessage(Message<E> message) {
        try {
            messageListener.onMessage(message.getMessageObject());
        } catch (ClassCastException e) {
            // EMPTY
        }
    }
}
