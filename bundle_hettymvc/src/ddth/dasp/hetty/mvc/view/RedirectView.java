package ddth.dasp.hetty.mvc.view;

import ddth.dasp.hetty.message.protobuf.HettyProtoBuf;
import ddth.dasp.hetty.message.protobuf.ResponseUtils;
import ddth.dasp.hetty.qnt.ITopicPublisher;

public class RedirectView implements IView {

    private String url;

    public RedirectView(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    public void render(HettyProtoBuf.Request request, Object model, ITopicPublisher topicPublisher) {
        HettyProtoBuf.Response response = ResponseUtils.response301(request, getUrl()).build();
        topicPublisher.publishToTopic(response);
    }
}
