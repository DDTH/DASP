package ddth.dasp.hetty.message.protobuf;

public interface IRequestParser {
    public String getAction(HettyProtoBuf.Request requestProtobuf);

    public String getModule(HettyProtoBuf.Request requestProtobuf);

    public String getPathParam(HettyProtoBuf.Request requestProtobuf, int index);

    public String getUrlParam(HettyProtoBuf.Request requestProtobuf, String name);
}
