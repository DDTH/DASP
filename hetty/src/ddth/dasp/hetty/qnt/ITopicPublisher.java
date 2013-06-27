package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

public interface ITopicPublisher {
    public boolean publish(String topicName, Object obj);

    public boolean publish(String topicName, Object obj, long timeout, TimeUnit timeUnit);
}
