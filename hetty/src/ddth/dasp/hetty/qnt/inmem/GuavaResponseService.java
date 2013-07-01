package ddth.dasp.hetty.qnt.inmem;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import ddth.dasp.hetty.front.AbstractHettyResponseService;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.utils.GuavaUtils;

/*
 * TODO: what would happen if a message comes when onMessage() is busy?
 */
public class GuavaResponseService extends AbstractHettyResponseService {
    @Subscribe
    @AllowConcurrentEvents
    public void handleEvent(IResponse response) {
        writeResponse(response);
    }

    public void init() {
        GuavaUtils.EVENT_BUS.register(this);
    }

    public void destroy() {
        GuavaUtils.EVENT_BUS.unregister(this);
    }
}
