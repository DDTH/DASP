package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface IQueueReader {
    public Object queueRead(String queueName);

    public Object queueRead(String queueName, long timeout, TimeUnit timeUnit);
}
