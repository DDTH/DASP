package ddth.dasp.framework.cache;

import java.io.Serializable;

public class CacheEntry implements Serializable {

    private static final long serialVersionUID = "$Id$".hashCode();

    private String key;
    private Object value;
    private long creationTimestamp = System.currentTimeMillis(), lastAccessTimestamp = System
            .currentTimeMillis(), expireAfterWrite = -1, expireAfterAccess = -1;

    public CacheEntry() {
    }

    public CacheEntry(String key, Object value) {
        setKey(key);
        setValue(value);
    }

    public CacheEntry(String key, Object value, long expireAfterWrite, long expireAfterAccess) {
        setKey(key);
        setValue(value);
        setExpireAfterAccess(expireAfterAccess);
        setExpireAfterWrite(expireAfterWrite);
    }

    public boolean isExpired() {
        if (expireAfterWrite >= 0) {
            return creationTimestamp + expireAfterWrite * 1000L > System.currentTimeMillis();
        }
        if (expireAfterAccess >= 0) {
            return lastAccessTimestamp + expireAfterAccess * 1000L > System.currentTimeMillis();
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        if (!isExpired()) {
            lastAccessTimestamp = System.currentTimeMillis();
            return value;
        }
        return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public long getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }
}
