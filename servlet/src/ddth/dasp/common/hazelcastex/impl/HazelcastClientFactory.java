package ddth.dasp.common.hazelcastex.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.hazelcastex.PoolConfig;

public class HazelcastClientFactory extends AbstractHazelcastClientFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(HazelcastClientFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected HazelcastClientPool buildHazelcastClientPool(List<String> servers, String username,
            String password, PoolConfig poolConfig) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building a Hazelcast client pool {servers:" + servers + ";;username:"
                    + username + "}...");
        }
        HazelcastClientPoolableObjectFactory factory = new HazelcastClientPoolableObjectFactory(
                servers, username, password);
        HazelcastClientPool hazelcastClientPool = new HazelcastClientPool(factory, poolConfig);
        return hazelcastClientPool;
    }
}
