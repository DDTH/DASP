package ddth.dasp.common.hazelcastex;

import java.util.List;

/**
 * Factory to create {@link IHazelcastClient}s.
 * 
 * @author Thanh B. Nguyen <btnguyen2k@gmail.com>
 */
public interface IHazelcastClientFactory {

    public final static String GLOBAL_KEY = "ALL_HAZELCAST_FACTORIES";

    /**
     * Initializing method.
     */
    public void init();

    /**
     * Destruction method.
     */
    public void destroy();

    /**
     * Obtains a Hazelcast client with default maximum lifetime and default pool
     * settings.
     * 
     * @param servers
     *            list of Hazelcast servers in format <code>host:port</code>
     * @return
     */
    public IHazelcastClient getHazelcastClient(List<String> servers);

    /**
     * Obtains a Hazelcast client with default maximum lifetime and default pool
     * settings.
     * 
     * @param servers
     *            list of Hazelcast servers in format <code>host:port</code>
     * @param username
     * @param password
     * @return
     */
    public IHazelcastClient getHazelcastClient(List<String> servers, String username,
            String password);

    /**
     * Obtains a Hazelcast client with default maximum lifetime and default pool
     * settings.
     * 
     * @param servers
     *            list of Hazelcast servers in format <code>host:port</code>
     * @param username
     * @param password
     * @param poolConfig
     * @return
     */
    public IHazelcastClient getHazelcastClient(List<String> servers, String username,
            String password, PoolConfig poolConfig);

    /**
     * Returns a Hazelcast client after use.
     * 
     * @param hazelcastClient
     */
    public void returnHazelcastClient(IHazelcastClient hazelcastClient);
}
