package ddth.dasp.common.hazelcastex;

/**
 * Pool configuration.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class PoolConfig {

    /**
     * Default lifetime (ms) of an open connection.
     */
    public final static long DEFAULT_MAX_CONNECTION_LIFETIME = -1;

    /**
     * Default wait time (ms) the pool will wait to obtain a connection.
     */
    public final static long DEFAULT_MAX_WAIT_TIME = 3000;

    /**
     * Default maximum number of active connections.
     */
    public final static int DEFAULT_MAX_ACTIVE = 8;

    /**
     * Default maximum number of idle connections.
     */
    public final static int DEFAULT_MAX_IDLE = 4;

    /**
     * Default minimum number of idle connections.
     */
    public final static int DEFAULT_MIN_IDLE = 1;

    private int maxActive = DEFAULT_MAX_ACTIVE, maxIdle = DEFAULT_MAX_IDLE,
            minIdle = DEFAULT_MIN_IDLE;
    private long maxWaitTime = DEFAULT_MAX_WAIT_TIME;

    public int getMaxActive() {
        return maxActive;
    }

    public PoolConfig setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public PoolConfig setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public PoolConfig setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public PoolConfig setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        return this;
    }

    public static PoolConfig newInstance() {
        return new PoolConfig();
    }
}
