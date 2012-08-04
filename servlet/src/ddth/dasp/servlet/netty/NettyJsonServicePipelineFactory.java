package ddth.dasp.servlet.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.util.Timer;

public class NettyJsonServicePipelineFactory implements ChannelPipelineFactory {

    private final Timer timer;

    public NettyJsonServicePipelineFactory(Timer timer) {
        this.timer = timer;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        // ChannelPipeline pipeline = Channels.pipeline(new
        // WriteTimeoutHandler(timer, 5));
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(128 * 1024));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", new NettyJsonServiceHandler());
        return pipeline;
    }
}
