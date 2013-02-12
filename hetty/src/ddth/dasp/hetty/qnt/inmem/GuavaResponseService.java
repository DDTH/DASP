package ddth.dasp.hetty.qnt.inmem;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import ddth.dasp.hetty.front.AbstractHettyResponseService;
import ddth.dasp.hetty.message.protobuf.HettyProtoBuf;
import ddth.dasp.hetty.utils.GuavaUtils;

public class GuavaResponseService extends AbstractHettyResponseService {
    @Subscribe
    @AllowConcurrentEvents
    public void handleEvent(HettyProtoBuf.Response responseProtobuf) {
        writeResponse(responseProtobuf);
    }

    public void init() {
        GuavaUtils.EVENT_BUS.register(this);
    }

    public void destroy() {
    }
}
