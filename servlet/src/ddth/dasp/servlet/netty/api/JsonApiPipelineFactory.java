package ddth.dasp.servlet.netty.api;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

public class JsonApiPipelineFactory implements ChannelPipelineFactory {

    private final ChannelHandler idleStateHandler;
    private final int maxRequestSize;

    public JsonApiPipelineFactory(Timer timer, long readTimeoutMillisecs,
            long writeTimeoutMillisecs, int maxRequestSize) {
        this.idleStateHandler = new IdleStateHandler(timer, readTimeoutMillisecs,
                writeTimeoutMillisecs, 0, TimeUnit.MILLISECONDS);
        this.maxRequestSize = maxRequestSize;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(maxRequestSize));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("timeout", idleStateHandler);
        pipeline.addLast("handler", new JsonApiHandler());
        return pipeline;
    }
}
