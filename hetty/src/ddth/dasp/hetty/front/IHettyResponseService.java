package ddth.dasp.hetty.front;

import ddth.dasp.hetty.message.HettyProtoBuf;

public interface IHettyResponseService {
    public void writeResponse(HettyProtoBuf.Response responseProtobuf);
}
