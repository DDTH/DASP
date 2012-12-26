package ddth.dasp.hetty.qnt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InProcessQueue implements IQueueReader, IQueueWriter {

    private static BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();

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
    public boolean writeToQueue(Object value, long timeout, TimeUnit timeunit) {
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
    public Object readFromQueue() {
        return readFromQueue(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readFromQueue(long timeout, TimeUnit timeUnit) {
        try {
            return queue.poll(timeout, timeUnit);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
