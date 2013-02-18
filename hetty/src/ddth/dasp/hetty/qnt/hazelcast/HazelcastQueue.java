package ddth.dasp.hetty.qnt.hazelcast;

import java.util.concurrent.TimeUnit;

import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.hetty.qnt.IQueueReader;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class HazelcastQueue implements IQueueReader, IQueueWriter {

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
        return hazelcastClientFactory.writeToQueue(hazelcastQueueName, value, timeout, timeUnit);
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
        return hazelcastClientFactory.readFromQueue(hazelcastQueueName, timeout, timeUnit);
    }
}
