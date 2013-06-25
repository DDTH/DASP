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
    public boolean deleteAllMapEntries(String mapName);

    /**
     * Deletes a value from a map.
     * 
     * @param mapName
     * @param key
     */
    public boolean deleteFromMap(String mapName, String key);

    /**
     * Updates TTL of a map entry.
     * 
     * @param mapName
     * @param key
     * @param ttlSeconds
     */
    public boolean expireMapEntry(String mapName, String key, int ttlSeconds);

    /**
     * Gets a value from a map.
     * 
     * @param mapName
     * @param value
     * @param ttlSeconds
     * @return
     */
    public Object getFromMap(String mapName, String key);

    /**
     * Puts a value to a map.
     * 
     * @param mapName
     * @param key
     * @param value
     * @param ttlSeconds
     */
    public boolean putToMap(String mapName, String key, Object value, int ttlSeconds);

    /**
     * Gets an item from queue.
     * 
     * @param queueName
     * @return
     */
    public Object getFromQueue(String queueName);

    /**
     * Gets an item from queue.
     * 
     * @param queueName
     * @param timeout
     * @param timeoutTimeUnit
     * @return
     */
    public Object getFromQueue(String queueName, long timeout, TimeUnit timeoutTimeUnit);

    /**
     * Puts an item to a queue.
     * 
     * @param queueName
     * @param value
     * @return
     */
    public boolean putToQueue(String queueName, String value);

    /**
     * Puts an item to queue.
     * 
     * @param queueName
     * @param value
     * @param timeout
     * @param timeoutTimeUnit
     * @return
     */
    public boolean putToQueue(String queueName, String value, long timeout, TimeUnit timeoutTimeUnit);

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
