package ddth.dasp.hetty.mvc.view;

import ddth.dasp.hetty.message.protobuf.HettyProtoBuf;
import ddth.dasp.hetty.qnt.ITopicPublisher;

/**
 * The "view" part in MVC.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IView {
    public void render(HettyProtoBuf.Request request, Object model, ITopicPublisher topicPublisher)
            throws Exception;
}
