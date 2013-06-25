package ddth.dasp.common.hazelcastex.impl;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientPool;
import ddth.dasp.common.hazelcastex.PoolConfig;

public class HazelcastClientPool extends GenericObjectPool<AbstractHazelcastClient> implements
        IHazelcastClientPool {

    private Logger LOGGER = LoggerFactory.getLogger(HazelcastClientPool.class);
    private PoolConfig poolConfig;

    public HazelcastClientPool(PoolableObjectFactory<AbstractHazelcastClient> factory,
            PoolConfig poolConfig) {
        super(factory);
        setPoolConfig(poolConfig);
        setTestOnBorrow(true);
        setTestWhileIdle(true);
    }

    public HazelcastClientPool setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        if (poolConfig != null) {
            int maxActive = poolConfig != null ? poolConfig.getMaxActive()
                    : PoolConfig.DEFAULT_MAX_ACTIVE;
            long maxWaitTime = poolConfig != null ? poolConfig.getMaxWaitTime()
                    : PoolConfig.DEFAULT_MAX_WAIT_TIME;
            int maxIdle = poolConfig != null ? poolConfig.getMaxIdle()
                    : PoolConfig.DEFAULT_MAX_IDLE;
            int minIdle = poolConfig != null ? poolConfig.getMinIdle()
                    : PoolConfig.DEFAULT_MIN_IDLE;
            LOGGER.debug("Updating Hazelcast client pool {maxActive:" + maxActive + ";maxWait:"
                    + maxWaitTime + ";minIdle:" + minIdle + ";maxIdle:" + maxIdle + "}...");
            this.setMaxActive(maxActive);
            this.setMaxIdle(maxIdle);
            this.setMaxWait(maxWaitTime);
            this.setMinIdle(minIdle);
        }
        return this;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractHazelcastClient borrowObject() throws Exception {
        AbstractHazelcastClient hazelcastClient = super.borrowObject();
        if (hazelcastClient != null) {
            hazelcastClient.setHazelcastClientPool(this);
        }
        return hazelcastClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IHazelcastClient borrowHazelcastClient() {
        try {
            return borrowObject();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void returnHazelcastClient(IHazelcastClient hazelcastClient) {
        if (hazelcastClient instanceof AbstractHazelcastClient)
            try {
                returnObject((AbstractHazelcastClient) hazelcastClient);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
    }
}
