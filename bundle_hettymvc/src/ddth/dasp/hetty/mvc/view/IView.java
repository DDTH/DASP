package ddth.dasp.hetty.mvc.view;

import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.qnt.ITopicPublisher;

/**
 * The "view" part in MVC.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IView {
    public void render(IRequest request, Object model, ITopicPublisher topicPublisher,
            String topicName) throws Exception;
}
