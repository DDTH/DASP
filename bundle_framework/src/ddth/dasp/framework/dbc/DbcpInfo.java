package ddth.dasp.framework.dbc;

/**
 * Database Connection Pool Info.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DbcpInfo {

	/**
	 * Default lifetime (ms) of an opened DB connection.
	 */
	public final static long DEFAULT_MAX_CONNECTION_LIFETIME = -1;

	/**
	 * Default wait time (ms) the pool will wait to obtain a DB connection.
	 */
	public final static long DEFAULT_MAX_WAIT_TIME = 3000;

	/**
	 * Default maximum number of active DB connections.
	 */
	public final static int DEFAULT_MAX_ACTIVE = 8;

	/**
	 * Default maximum number of idle DB connections.
	 */
	public final static int DEFAULT_MAX_IDLE = 4;

	/**
	 * Default minimum number of idle DB connections.
	 */
	public final static int DEFAULT_MIN_IDLE = 1;

	private int maxActive = DEFAULT_MAX_ACTIVE, maxIdle = DEFAULT_MAX_IDLE,
			minIdle = DEFAULT_MIN_IDLE;
	private long maxWaitTime = DEFAULT_MAX_WAIT_TIME;

	public int getMaxActive() {
		return maxActive;
	}

	public DbcpInfo setMaxActive(int maxActive) {
		this.maxActive = maxActive;
		return this;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public DbcpInfo setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
		return this;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public DbcpInfo setMinIdle(int minIdle) {
		this.minIdle = minIdle;
		return this;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public DbcpInfo setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
		return this;
	}

	public static DbcpInfo newInstance() {
		return new DbcpInfo();
	}

}
