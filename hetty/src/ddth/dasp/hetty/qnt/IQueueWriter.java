package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface IQueueWriter {
    public boolean queueWrite(String queueName, Object value);

    public boolean queueWrite(String queueName, Object value, long timeout, TimeUnit timeUnit);
}
