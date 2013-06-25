package ddth.dasp.common.hazelcastex.impl;

import java.util.List;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class HazelcastClientPoolableObjectFactory extends
        BasePoolableObjectFactory<AbstractHazelcastClient> {

    private List<String> hazelcastServers;
    public String hazelcastUser, hazelcastPassword;

    public HazelcastClientPoolableObjectFactory(List<String> servers, String username,
            String password) {
        hazelcastServers = servers;
        hazelcastUser = username;
        hazelcastPassword = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateObject(AbstractHazelcastClient hazelcastClient) throws Exception {
        // hazelcastClient.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyObject(AbstractHazelcastClient hazelcastClient) throws Exception {
        hazelcastClient.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractHazelcastClient makeObject() throws Exception {
        AbstractHazelcastClient hazelcastClient = new PoolableHazelcastClient();
        hazelcastClient.setHazelcastServers(hazelcastServers).setHazelcastUsername(hazelcastUser)
                .setHazelcastPassword(hazelcastPassword);
        hazelcastClient.init();
        return hazelcastClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivateObject(AbstractHazelcastClient hazelcastClient) throws Exception {
        // EMPTY
        // hazelcastClient.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateObject(AbstractHazelcastClient hazelcastClient) {
        try {
            return hazelcastClient.ping();
        } catch (Exception e) {
            return false;
        }
    }
}
