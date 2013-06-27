package ddth.dasp.hetty.qnt.inmem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ddth.dasp.hetty.qnt.IQueueReader;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class InMemoryQueue implements IQueueReader, IQueueWriter {

    private static BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();

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
    public boolean queueWrite(String queueName, Object value, long timeout, TimeUnit timeunit) {
        try {
            return queue.offer(value, timeout, timeunit);
        } catch (InterruptedException e) {
            return false;
        }
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
        try {
            return queue.poll(timeout, timeUnit);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
