package ddth.dasp.hetty.message;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface IMessageFactory {
    public IRequest buildRequest(HttpRequest httpRequest, int channelId, byte[] requestContent);

    public IRequest deserializeRequest(byte[] serializedData);

    public IResponse deserializeResponse(byte[] serializedData);
}
