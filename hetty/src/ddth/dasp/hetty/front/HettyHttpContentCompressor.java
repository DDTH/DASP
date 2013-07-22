package ddth.dasp.hetty.front;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpMessage;

import ddth.dasp.hetty.message.protobuf.ResponseUtils;

public class HettyHttpContentCompressor extends HttpContentCompressor {

    public HettyHttpContentCompressor() {
        // compression with minimum memory usage
        super(1, 9, 1);
    }

    protected EncoderEmbedder<ChannelBuffer> newContentEncoder(HttpMessage msg,
            String acceptEncoding) throws Exception {
        String contentType = msg.getHeader(ResponseUtils.HEADER_CONTENT_TYPE);
        if (contentType != null && contentType.startsWith("text/")) {
            return super.newContentEncoder(msg, acceptEncoding);
        }
        return null;
    }
}
