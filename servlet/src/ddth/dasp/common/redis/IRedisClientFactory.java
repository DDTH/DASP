package ddth.dasp.common.redis;

/**
 * Factory to create {@link IRedisClient}s.
 * 
 * @author Thanh B. Nguyen <btnguyen2k@gmail.com>
 */
public interface IRedisClientFactory {

    public final static String GLOBAL_KEY = "ALL_REDIS_FACTORIES";

    /**
     * Initializing method.
     */
    public void init();

    /**
     * Destruction method.
     */
    public void destroy();

    /**
     * Obtains a Redis client with default maximum lifetime and default pool
     * settings.
     * 
     * @param host
     * @return
     */
    public IRedisClient getRedisClient(String host);

    /**
     * Obtains a Redis client with default maximum lifetime and default pool
     * settings.
     * 
     * @param host
     * @param port
     * @return
     */
    public IRedisClient getRedisClient(String host, int port);

    /**
     * Obtains a Redis client with default maximum lifetime and default pool
     * settings.
     * 
     * @param host
     * @param username
     * @param password
     * @return
     */
    public IRedisClient getRedisClient(String host, int port, String username, String password);

    /**
     * Obtains a Redis client with default maximum lifetime and default pool
     * settings.
     * 
     * @param host
     * @param username
     * @param password
     * @param poolConfig
     * @return
     */
    public IRedisClient getRedisClient(String host, int port, String username, String password,
            PoolConfig poolConfig);

    /**
     * Returns a Redis client after use.
     * 
     * @param redisClient
     */
    public void returnRedisClient(IRedisClient redisClient);
}
