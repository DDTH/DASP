package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface IQueueReader {
    public Object readFromQueue();

    public Object readFromQueue(long timeout, TimeUnit timeUnit);
}
