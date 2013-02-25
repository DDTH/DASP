package ddth.dasp.hetty.qnt.hazelcast;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.hetty.qnt.IQueueReader;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class HazelcastQueue implements IQueueReader, IQueueWriter {

    private final Logger LOGGER = LoggerFactory.getLogger(HazelcastQueue.class);
    private IHazelcastClientFactory hazelcastClientFactory;
    private String hazelcastQueueName;

    protected String getHazelcastQueueName() {
        return hazelcastQueueName;
    }

    public void setHazelcastQueueName(String hazelcastQueueName) {
        this.hazelcastQueueName = hazelcastQueueName;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public void init() {
    }

    public void destroy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeToQueue(Object value) {
        return writeToQueue(value, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean writeToQueue(Object value, long timeout, TimeUnit timeUnit) {
        try {
            return hazelcastClientFactory
                    .writeToQueue(hazelcastQueueName, value, timeout, timeUnit);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readFromQueue() {
        return readFromQueue(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readFromQueue(long timeout, TimeUnit timeUnit) {
        try {
            return hazelcastClientFactory.readFromQueue(hazelcastQueueName, timeout, timeUnit);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }
}
