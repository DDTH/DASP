package ddth.dasp.common.hazelcast;

import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.MessageListener;

public interface IHazelcastClientFactory {
    public HazelcastClient getHazelcastClient();

    public void returnHazelcastClient();

    /**
     * Gets number of items of a queue.
     * 
     * @param queueName
     * @return
     */
    public int getQueueSize(String queueName);

    /**
     * Reads from a queue, with default timeout.
     * 
     * @param queueName
     * @return
     */
    public Object readFromQueue(String queueName);

    /**
     * Reads from a queue, with specified timeout.
     * 
     * @param queueName
     * @param timeout
     * @param timeUnit
     * @return
     */
    public Object readFromQueue(String queueName, long timeout, TimeUnit timeUnit);

    /**
     * Writes to a queue, with default timeout.
     * 
     * @param queueName
     * @param value
     * @return
     */
    public boolean writeToQueue(String queueName, Object value);

    /**
     * Writes to a queue, with specified timeout.
     * 
     * @param queueName
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean writeToQueue(String queueName, Object value, long timeout, TimeUnit timeUnit);

    /**
     * Subscribes to a topic.
     * 
     * @param topicName
     * @param messageListener
     */
    public <E> void subcribeToTopic(String topicName, MessageListener<E> messageListener);

    /**
     * Publishes a message to a topic.
     * 
     * @param topicName
     * @param message
     */
    public void publishToTopic(String topicName, Object message);

    /**
     * Unsubscribes from a topic.
     * 
     * @param topic
     * @param messageListener
     */
    public <E> void unsubscribeFromTopic(String topicName, MessageListener<E> messageListener);
}
