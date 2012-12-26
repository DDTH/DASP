package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface IQueueWriter {
    public boolean writeToQueue(Object value);

    public boolean writeToQueue(Object value, long timeout, TimeUnit timeunit);
}
