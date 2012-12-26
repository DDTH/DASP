package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface ITopicPublisher {
    public boolean publishToTopic(Object obj);

    public boolean publishToTopic(Object obj, long timeout, TimeUnit timeunit);
}
