package ddth.dasp.hetty.qnt.hazelcast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.PoolConfig;
import ddth.dasp.hetty.qnt.IQueueReader;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class HazelcastQueue implements IQueueReader, IQueueWriter {

    private final Logger LOGGER = LoggerFactory.getLogger(HazelcastQueue.class);
    private IHazelcastClientFactory hazelcastClientFactory;
    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;
    private PoolConfig poolConfig;
    private int queueSizeThreshold = 1000;
    private Set<IHazelcastClient> allocatedHazelcastClients = new HashSet<IHazelcastClient>();

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public HazelcastQueue setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
        return this;
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public HazelcastQueue setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public HazelcastQueue setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public HazelcastQueue setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public HazelcastQueue setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    protected int getQueueSizeThreshold() {
        return queueSizeThreshold;
    }

    public HazelcastQueue setQueueSizeThreshold(int queueSizeThreshold) {
        this.queueSizeThreshold = queueSizeThreshold;
        return this;
    }

    private IHazelcastClient getHazelcastClient() {
        IHazelcastClient hazelcastClient = hazelcastClientFactory.getHazelcastClient(
                hazelcastServers, hazelcastUsername, hazelcastPassword, poolConfig);
        if (hazelcastClient != null) {
            allocatedHazelcastClients.add(hazelcastClient);
        }
        return hazelcastClient;
    }

    private void returnHazelcastClient(IHazelcastClient hazelcastClient) {
        if (hazelcastClient != null) {
            try {
                allocatedHazelcastClients.remove(hazelcastClient);
            } finally {
                hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
            }
        }
    }

    public void init() {
    }

    public void destroy() {
        for (IHazelcastClient hazelcastClient : allocatedHazelcastClients) {
            try {
                hazelcastClientFactory.returnHazelcastClient(hazelcastClient);
            } catch (Exception e) {
            }
        }
        allocatedHazelcastClients.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueWrite(String queueName, Object value) {
        return queueWrite(queueName, value, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean queueWrite(String queueName, Object value, long timeout, TimeUnit timeUnit) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                int queueSize = hazelcastClient.queueSize(queueName);
                if (queueSize < 0 || queueSize > queueSizeThreshold) {
                    LOGGER.warn("Queue not available or full!");
                    return false;
                }
                return hazelcastClient.queuePush(queueName, value, timeout, timeUnit);
            } finally {
                returnHazelcastClient(hazelcastClient);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object queueRead(String queueName) {
        return queueRead(queueName, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object queueRead(String queueName, long timeout, TimeUnit timeUnit) {
        IHazelcastClient hazelcastClient = getHazelcastClient();
        try {
            return hazelcastClient != null ? hazelcastClient
                    .queuePoll(queueName, timeout, timeUnit) : null;
        } finally {
            returnHazelcastClient(hazelcastClient);
        }
    }
}
