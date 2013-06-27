package ddth.dasp.hetty.qnt.inmem;

import java.util.concurrent.TimeUnit;

import ddth.dasp.hetty.qnt.ITopicPublisher;
import ddth.dasp.hetty.utils.GuavaUtils;

public class GuavaTopicPublisher implements ITopicPublisher {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(String topicName, Object obj) {
        return publish(topicName, obj, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(String topicName, Object obj, long timeout, TimeUnit timeunit) {
        GuavaUtils.EVENT_BUS.post(obj);
        return true;
    }
}
