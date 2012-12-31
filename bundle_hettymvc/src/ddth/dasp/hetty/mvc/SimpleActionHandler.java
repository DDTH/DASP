package ddth.dasp.hetty.mvc;

/**
 * This action handler simply returns the view name.
 */
import ddth.dasp.hetty.message.protobuf.HettyProtoBuf.Request;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public class SimpleActionHandler extends AbstractActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object internalHandleRequest(Request request, ITopicPublisher topicPublisher) {
        return getViewName();
    }
}
