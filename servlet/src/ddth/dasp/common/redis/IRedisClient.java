package ddth.dasp.common.redis;

import java.util.Set;

/**
 * A Redis (http://redis.io) client API.
 * 
 * @author Thanh B. Nguyen <btnguyen2k@gmail.com>
 */
public interface IRedisClient {

    public final static int DEFAULT_REDIS_PORT = 6379;
    public final static int DEFAULT_READ_TIMEOUT = 10;

    /**
     * Initializes this Redis client before use. The Redis client is not usable
     * until this method is called.
     */
    public void init();

    /**
     * Destroys this Redis client. The Redis client is no longer usable after
     * calling this method.
     */
    public void destroy();

    /**
     * Closes this Redis client, but donot destroy it.
     */
    public void close();

    /* Redis API */
    /**
     * "Ping" the Redis server.
     * 
     * @return
     */
    public String ping();

    /**
     * Updates expiry time of a Redis key.
     * 
     * @param key
     * @param ttlSeconds
     *            time to live (a.k.a "expiry after write") in seconds
     */
    public void expire(String key, int ttlSeconds);

    /**
     * Deletes value(s) from Redis server.
     * 
     * @param keys
     */
    public void delete(String... keys);

    /**
     * Gets a value from Redis server.
     * 
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * Gets a value from Redis server.
     * 
     * @param key
     * @return
     */
    public byte[] getAsBinary(String key);

    /**
     * Sets a value to Redis server.
     * 
     * @param key
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expiry after write") in seconds
     */
    public void set(String key, String value, int ttlSeconds);

    /**
     * Sets a value to Redis server.
     * 
     * @param key
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expiry after write") in seconds
     */
    public void set(String key, byte[] value, int ttlSeconds);

    /**
     * Deletes values from a Redis hash.
     * 
     * @param mapName
     * @param fieldNames
     */
    public void hashDelete(String mapName, String... fieldNames);

    /**
     * Gets number of elements of a Redis hash.
     * 
     * @param mapName
     * @return
     */
    public long hashSize(String mapName);

    /**
     * Gets a field value from a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @return
     */
    public String hashGet(String mapName, String fieldName);

    /**
     * Gets a field value from a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @return
     */
    public byte[] hashGetAsBinary(String mapName, String fieldName);

    /**
     * Sets a field value of a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expiry after write") in seconds
     */
    public void hashSet(String mapName, String fieldName, String value, int ttlSeconds);

    /**
     * Sets a field value of a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expiry after write") in seconds
     */
    public void hashSet(String mapName, String fieldName, byte[] value, int ttlSeconds);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param messages
     */
    public void listPush(String listName, String... messages);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param messages
     */
    public void listPush(String listName, byte[]... message);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param ttlSeconds
     * @param messages
     */
    public void listPush(String listName, int ttlSeconds, String... messages);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param ttlSeconds
     * @param messages
     */
    public void listPush(String listName, int ttlSeconds, byte[]... messages);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @return
     */
    public String listPop(String listName);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @return
     */
    public String listPop(String listName, boolean block);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @param timeout
     *            timeout in seconds
     * @return
     */
    public String listPop(String listName, boolean block, int timeout);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @return
     */
    public byte[] listPopAsBinary(String listName);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @return
     */
    public byte[] listPopAsBinary(String listName, boolean block);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @param timeout
     *            timeout in seconds
     * @return
     */
    public byte[] listPopAsBinary(String listName, boolean block, int timeout);

    /**
     * Gets a list's size.
     * 
     * @param listName
     * @return
     */
    public long listSize(String listName);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param messages
     */
    public void setAdd(String setName, String... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param messages
     */
    public void setAdd(String setName, byte[]... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param ttlSeconds
     * @param messages
     */
    public void setAdd(String setName, int ttlSeconds, String... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param ttlSeconds
     * @param messages
     */
    public void setAdd(String setName, int ttlSeconds, byte[]... messages);

    /**
     * Checks if a value is a member of a set.
     * 
     * @param setName
     * @param value
     * @return
     */
    public boolean setIsMember(String setName, String value);

    /**
     * Randomly removes and returns an element from a set.
     * 
     * @param setName
     * @return
     */
    public String setPop(String setName);

    /**
     * Randomly removes and returns an element from a set as a binary string.
     * 
     * @param setName
     * @return
     */
    public byte[] setPopAsBinary(String setName);

    /**
     * Gets all members of a set.
     * 
     * @param setName
     * @return
     */
    public Set<String> setMembers(String setName);

    /**
     * Gets all members of a set as binary.
     * 
     * @param setName
     * @return
     */
    public Set<byte[]> setMembersAsBinary(String setName);

    /**
     * Removes value(s) from a set.
     * 
     * @param setName
     * @param members
     */
    public void setRemove(String setName, String... members);

    /**
     * Removes a value from a set.
     * 
     * @param setName
     * @param members
     */
    public void setRemove(String setName, byte[]... members);

    /**
     * Checks if a value is a member of a set.
     * 
     * @param setName
     * @param value
     * @return
     */
    public boolean setIsMember(String setName, byte[] value);

    /**
     * Gets size of a set.
     * 
     * @param setName
     * @return
     */
    public long setSize(String setName);

    /**
     * Publishes a message to a channel.
     * 
     * @param channelName
     * @param message
     */
    public void publish(String channelName, String message);

    /**
     * Publishes a message to a channel.
     * 
     * @param channelName
     * @param message
     */
    public void publish(String channelName, byte[] message);

    /**
     * Subscribes to a channel.
     * 
     * Note: This method is a blocking operation!
     * 
     * @param channelName
     * @param messageListener
     * @return
     */
    public boolean subscribe(String channelName, IMessageListener messageListener);

    /**
     * Unsubscribes from a channel.
     * 
     * @param channelName
     * @param messageListener
     * @return
     */
    public boolean unsubscribe(String channelName, IMessageListener messageListener);
    /* Redis API */
}
