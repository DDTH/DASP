package ddth.dasp.hetty.mvc.view;

import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.IResponse;
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
    public void render(IRequest request, Object model, ITopicPublisher topicPublisher) {
        IResponse response = ResponseUtils.response301(request, getUrl());
        topicPublisher.publishToTopic(response);
    }
}
