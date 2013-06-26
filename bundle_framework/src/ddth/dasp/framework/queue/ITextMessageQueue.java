package ddth.dasp.framework.queue;

/**
 * Represents a text-based message queue.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface ITextMessageQueue {
    public String getQueueName();

    /**
     * Consumes a message from the queue. Returns <code>null</code> if the queue
     * is empty.
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public String consumeMessage(String message) throws Exception;

    /**
     * Consumes a message from the queue, waits up to
     * <code>timeoutMillisecs</code> milliseconds for the message to be
     * available.
     * 
     * @param message
     * @param timeoutMillisecs
     *            timeout in milliseconds
     * @return
     * @throws Exception
     */
    public String consumeMessage(String message, long timeoutMillisecs) throws Exception;

    /**
     * Produces a message to the queue. Returns <code>true</code> upon success,
     * <code>false</code> otherwise.
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public boolean produceMessage(String message) throws Exception;

    /**
     * Produces a message to the queue, waits up to
     * <code>timeoutMillisecs</code> milliseconds if the queue is currently
     * full. Returns <code>true</code> upon success, <code>false</code>
     * otherwise.
     * 
     * @param message
     * @param timeoutMillisecs
     *            timeout in milliseconds
     * @return
     * @throws Exception
     */
    public boolean produceMessage(String message, long timeoutMillisecs) throws Exception;
}
