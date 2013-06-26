package ddth.dasp.framework.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * In-process implementation of {@link ITextMessageQueue}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class InProcessTextMessageQueue extends AbstractTextMessageQueue {

    private int capacity = -1;
    private BlockingQueue<String> queue;

    public void init() {
        queue = capacity > 0 ? new LinkedBlockingQueue<String>(capacity)
                : new LinkedBlockingQueue<String>();
    }

    public void destroy() {
        if (queue != null) {
            queue.clear();
            queue = null;
        }
    }

    public InProcessTextMessageQueue setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String consumeMessage(String message) {
        return queue.poll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String consumeMessage(String message, long timeoutMillisecs) {
        try {
            return queue.poll(timeoutMillisecs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean produceMessage(String message) throws Exception {
        return queue.offer(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean produceMessage(String message, long timeoutMillisecs) throws Exception {
        return queue.offer(message, timeoutMillisecs, TimeUnit.MILLISECONDS);
    }
}
