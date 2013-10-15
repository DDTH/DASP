package ddth.dasp.common.redis.impl.jedis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;
import ddth.dasp.common.redis.IMessageListener;
import ddth.dasp.common.redis.impl.AbstractRedisClient;

public class PoolableRedisClient extends AbstractRedisClient {

    private Jedis redisClient;
    private final Map<String, Set<IMessageListener>> topicSubscriptions = new ConcurrentHashMap<String, Set<IMessageListener>>();
    private final Map<IMessageListener, WrappedJedisPubSub> topicSubscriptionMappings = new ConcurrentHashMap<IMessageListener, WrappedJedisPubSub>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        int timeout = 10000; // timeout in milliseconds
        // Jedis passes timeout value to a java.net.Socket, hence it should be
        // in milliseconds
        redisClient = new Jedis(getRedisHost(), getRedisPort(), timeout);
        if (!StringUtils.isBlank(getRedisPassword())) {
            redisClient.auth(getRedisPassword());
        }
        redisClient.connect();
        // System.out.println("\tNEW REDIS CLIENT");
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        try {
            redisClient.disconnect();
        } finally {
            redisClient.quit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (getRedisClientPool() != null) {
            getRedisClientPool().returnRedisClient(this);
        }
    }

    /* Redis API */
    /**
     * {@inheritDoc}
     */
    @Override
    public String ping() {
        return redisClient.isConnected() ? redisClient.ping() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expire(String key, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.expire(key, ttlSeconds);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String... keys) {
        redisClient.del(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String key) {
        return redisClient.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getAsBinary(String key) {
        return redisClient.get(SafeEncoder.encode(key));
    }

    /**
     * {@inheritDoc}
     */
    public void set(String key, String value, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.setex(key, ttlSeconds, value);
        } else {
            redisClient.set(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set(String key, byte[] value, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.setex(SafeEncoder.encode(key), ttlSeconds, value);
        } else {
            redisClient.set(SafeEncoder.encode(key), value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashDelete(String mapName, String... fieldName) {
        redisClient.hdel(mapName, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    public long hashSize(String mapName) {
        Long result = redisClient.hlen(mapName);
        return result != null ? result.longValue() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashGet(String mapName, String fieldName) {
        return redisClient.hget(mapName, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] hashGetAsBinary(String mapName, String fieldName) {
        return redisClient.hget(SafeEncoder.encode(mapName), SafeEncoder.encode(fieldName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashSet(String mapName, String fieldName, String value, int ttlSeconds) {
        redisClient.hset(mapName, fieldName, value);
        expire(mapName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashSet(String mapName, String fieldName, byte[] value, int ttlSeconds) {
        redisClient.hset(SafeEncoder.encode(mapName), SafeEncoder.encode(fieldName), value);
        expire(mapName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, String... messages) {
        listPush(listName, 0, messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, byte[]... messages) {
        listPush(listName, 0, messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, int ttlSeconds, String... messages) {
        redisClient.rpush(listName, messages);
        expire(listName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, int ttlSeconds, byte[]... messages) {
        redisClient.rpush(SafeEncoder.encode(listName), messages);
        expire(listName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName) {
        return listPop(listName, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName, boolean block) {
        return listPop(listName, block, DEFAULT_READ_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName, boolean block, int timeout) {
        if (!block) {
            return redisClient.lpop(listName);
        }
        List<String> result = redisClient.blpop(timeout, listName);
        return result != null && result.size() > 1 ? result.get(1) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName) {
        return listPopAsBinary(listName, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName, boolean block) {
        return listPopAsBinary(listName, block, DEFAULT_READ_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName, boolean block, int timeout) {
        if (!block) {
            return redisClient.lpop(SafeEncoder.encode(listName));
        }
        List<byte[]> result = redisClient.blpop(timeout, SafeEncoder.encode(listName));
        return result != null && result.size() > 1 ? result.get(1) : null;
    }

    /**
     * {@inheritDoc}
     */
    public long listSize(String listName) {
        Long size = redisClient.llen(listName);
        return size != null ? size.longValue() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listMembers(String listName) {
        long listSize = listSize(listName);
        List<String> result = redisClient.lrange(listName, 0, listSize - 1);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<byte[]> listMembersAsBinary(String listName) {
        long listSize = listSize(listName);
        List<byte[]> result = redisClient.lrange(SafeEncoder.encode(listName), 0, listSize - 1);
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @param messages
     */
    @Override
    public void setAdd(String setName, String... messages) {
        setAdd(setName, 0, messages);
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @param messages
     */
    @Override
    public void setAdd(String setName, byte[]... messages) {
        setAdd(setName, 0, messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, int ttlSeconds, String... messages) {
        redisClient.sadd(setName, messages);
        expire(setName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, int ttlSeconds, byte[]... messages) {
        redisClient.sadd(SafeEncoder.encode(setName), messages);
        expire(setName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @param value
     * @return
     */
    @Override
    public boolean setIsMember(String setName, String value) {
        Boolean result = redisClient.sismember(setName, value);
        return result != null ? result.booleanValue() : false;
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @param value
     * @return
     */
    @Override
    public boolean setIsMember(String setName, byte[] value) {
        Boolean result = redisClient.sismember(SafeEncoder.encode(setName), value);
        return result != null ? result.booleanValue() : false;
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @return
     */
    @Override
    public String setPop(String setName) {
        return redisClient.spop(setName);
    }

    /**
     * {@inheritDoc}
     * 
     * @param setName
     * @return
     */
    @Override
    public byte[] setPopAsBinary(String setName) {
        return redisClient.spop(SafeEncoder.encode(setName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> setMembers(String setName) {
        Set<String> result = redisClient.smembers(setName);
        return result != null ? result : new HashSet<String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<byte[]> setMembersAsBinary(String setName) {
        Set<byte[]> result = redisClient.smembers(SafeEncoder.encode(setName));
        return result != null ? result : new HashSet<byte[]>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRemove(String setName, String... members) {
        redisClient.srem(setName, members);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRemove(String setName, byte[]... members) {
        redisClient.srem(SafeEncoder.encode(setName), members);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long setSize(String setName) {
        Long size = redisClient.scard(setName);
        return size != null ? size.longValue() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(String channelName, String message) {
        redisClient.publish(channelName, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(String channelName, byte[] message) {
        redisClient.publish(SafeEncoder.encode(channelName), message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean subscribe(String channelName, IMessageListener messageListener) {
        Set<IMessageListener> subcription = topicSubscriptions.get(channelName);
        if (subcription == null) {
            subcription = new HashSet<IMessageListener>();
            topicSubscriptions.put(channelName, subcription);
        }
        boolean subscribe = false;
        WrappedJedisPubSub wrappedJedisPubSub = null;
        synchronized (subcription) {
            if (subcription.add(messageListener)) {
                wrappedJedisPubSub = new WrappedJedisPubSub(channelName, messageListener);
                topicSubscriptionMappings.put(messageListener, wrappedJedisPubSub);
                subscribe = true;
            }
        }
        if (subscribe) {
            byte[] channel = SafeEncoder.encode(channelName);
            // this operation blocks!
            redisClient.subscribe(wrappedJedisPubSub, channel);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unsubscribe(String channelName, IMessageListener messageListener) {
        Set<IMessageListener> subcription = topicSubscriptions.get(channelName);
        boolean unsubscribe = false;
        WrappedJedisPubSub wrappedJedisPubSub = null;
        if (subcription != null) {
            synchronized (subcription) {
                if (subcription.remove(messageListener)) {
                    wrappedJedisPubSub = topicSubscriptionMappings.remove(messageListener);
                    if (wrappedJedisPubSub != null) {
                        unsubscribe = true;
                    }
                }
            }
        }
        if (unsubscribe) {
            byte[] channel = SafeEncoder.encode(channelName);
            wrappedJedisPubSub.unsubscribe(channel);
            return true;
        } else {
            return false;
        }
    }
    /* Redis API */
}
