package ddth.dasp.framework.queue;

public abstract class AbstractTextMessageQueue implements ITextMessageQueue {
    private String queueName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueueName() {
        return queueName;
    }

    public AbstractTextMessageQueue setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }
}
