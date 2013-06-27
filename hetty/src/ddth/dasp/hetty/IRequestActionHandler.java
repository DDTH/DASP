package ddth.dasp.hetty;

import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public interface IRequestActionHandler {

    public final static String FILTER_KEY_MODULE = "Module";
    public final static String FILTER_KEY_ACTION = "Action";

    public void handleRequest(IRequest request, ITopicPublisher topicPublisher, String topicName)
            throws Exception;
}
