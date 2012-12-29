package ddth.dasp.servlet.netty.api;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

public class JsonApiPipelineFactory implements ChannelPipelineFactory {

    private final ChannelHandler idleStateHandler;

    public JsonApiPipelineFactory(Timer timer, long readTimeoutMillisecs, long writeTimeoutMillisecs) {
        this.idleStateHandler = new IdleStateHandler(timer, readTimeoutMillisecs,
                writeTimeoutMillisecs, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // pipeline.addLast("aggregator", new HttpChunkAggregator(128 * 1024));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("timeout", idleStateHandler);
        pipeline.addLast("handler", new JsonApiHandler());
        return pipeline;
    }
}
