package ddth.dasp.hetty.framework;

import ddth.dasp.hetty.message.HettyProtoBuf;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public interface IRequestActionHandler {

    public final static String FILTER_KEY_MODULE = "Module";
    public final static String FILTER_KEY_ACTION = "Action";

    public void handleRequest(HettyProtoBuf.Request request, ITopicPublisher topicPublisher);
}
