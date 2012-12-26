package ddth.dasp.hetty.qnt;

import java.util.concurrent.TimeUnit;

import ddth.dasp.hetty.utils.GuavaUtils;

public class GuavaTopicPublisher implements ITopicPublisher {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publishToTopic(Object obj) {
        return publishToTopic(obj, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publishToTopic(Object obj, long timeout, TimeUnit timeunit) {
        GuavaUtils.EVENT_BUS.post(obj);
        return true;
    }
}
