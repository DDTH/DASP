package ddth.dasp.common.hazelcastex;

import java.util.concurrent.TimeUnit;

/**
 * A Redis (http://redis.io) client API.
 * 
 * @author Thanh B. Nguyen <btnguyen2k@gmail.com>
 */
public interface IHazelcastClient {

    public final static int DEFAULT_HAZELCAST_PORT = 5701;
    public final static long DEFAULT_TIMEOUT = 3;
    public final static TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

    /**
     * Initializes this Hazelcast client before use. The Hazelcast client is not
     * usable until this method is called.
     */
    public void init();

    /**
     * Destroys this Hazelcast client. The Hazelcast client is no longer usable
     * after calling this method.
     */
    public void destroy();

    /**
     * Closes this Hazelcast client.
     */
    public void close();

    /* Hazelcast API */
    /**
     * "Pings" the Hazelcast server.
     * 
     * @return
     */
    public boolean ping();

    /**
     * Deletes all map entries.
     * 
     * @param mapName
     */
    public boolean mapDeleteAll(String mapName);

    /**
     * Deletes a value from a map.
     * 
     * @param mapName
     * @param key
     */
    public boolean mapDelete(String mapName, String key);

    /**
     * Updates TTL of a map entry.
     * 
     * @param mapName
     * @param key
     * @param ttlSeconds
     */
    public boolean mapSetExpiry(String mapName, String key, int ttlSeconds);

    /**
     * Gets a value from a map.
     * 
     * @param mapName
     * @param value
     * @param ttlSeconds
     * @return
     */
    public Object mapGet(String mapName, String key);

    /**
     * Puts a value to a map.
     * 
     * @param mapName
     * @param key
     * @param value
     * @param ttlSeconds
     */
    public boolean mapSet(String mapName, String key, Object value, int ttlSeconds);

    /**
     * Gets number of items of a map.
     * 
     * @param mapName
     * @return
     */
    public int mapSize(String mapName);

    /**
     * Polls an item from queue.
     * 
     * @param queueName
     * @return
     */
    public Object queuePoll(String queueName);

    /**
     * Polls an item from queue.
     * 
     * @param queueName
     * @param timeout
     * @param timeoutTimeUnit
     * @return
     */
    public Object queuePoll(String queueName, long timeout, TimeUnit timeoutTimeUnit);

    /**
     * Pushes an item to a queue.
     * 
     * @param queueName
     * @param value
     * @return
     */
    public boolean queuePush(String queueName, String value);

    /**
     * Pushes an item to queue.
     * 
     * @param queueName
     * @param value
     * @param timeout
     * @param timeoutTimeUnit
     * @return
     */
    public boolean queuePush(String queueName, String value, long timeout, TimeUnit timeoutTimeUnit);

    /**
     * Gets number of items of a queue.
     * 
     * @param queueName
     * @return
     */
    public int queueSize(String queueName);

    /**
     * Publishes to a topic.
     * 
     * @param topicName
     * @param value
     * @return
     */
    public boolean publish(String topicName, Object value);

    /**
     * Subscribes to a topic.
     * 
     * @param topicName
     * @param messageListener
     * @return
     */
    public <T> boolean subscribe(String topicName, IMessageListener<T> messageListener);

    /**
     * Unsubscribes from a topic.
     * 
     * @param topicName
     * @param messageListener
     * @return
     */
    public <T> boolean unsubscribe(String topicName, IMessageListener<T> messageListener);
    /* Hazelcast API */
}
