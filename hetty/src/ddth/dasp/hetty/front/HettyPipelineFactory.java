package ddth.dasp.hetty.front;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.Timer;

import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class HettyPipelineFactory implements ChannelPipelineFactory {

    private final ChannelHandler idleStateHandler;

    private ConcurrentMap<String, IQueueWriter> hostQueueWriterMapping = new ConcurrentHashMap<String, IQueueWriter>();
    private IMessageFactory messageFactory;

    public HettyPipelineFactory(Map<String, IQueueWriter> hostQueueWriterMapping,
            IMessageFactory messageFactory, Timer timer, long readTimeoutMillisecs,
            long writeTimeoutMillisecs) {
        if (hostQueueWriterMapping != null) {
            this.hostQueueWriterMapping.putAll(hostQueueWriterMapping);
        }
        this.messageFactory = messageFactory;
        this.idleStateHandler = new IdleStateHandler(timer, readTimeoutMillisecs,
                writeTimeoutMillisecs, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Maps a host-queueWriter.
     * 
     * @param host
     * @param queueWriter
     */
    public void mapHostQueueWriter(String host, IQueueWriter queueWriter) {
        hostQueueWriterMapping.put(host, queueWriter);
    }

    /**
     * Unmaps an existing host-queueWriter.
     * 
     * @param host
     */
    public void unmapHostQueueWriter(String host) {
        hostQueueWriterMapping.remove(host);
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // pipeline.addLast("aggregator", new HttpChunkAggregator(128 * 1024));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("timeout", idleStateHandler);
        // compression with minimum memory usage
        pipeline.addLast("deflater", new HttpContentCompressor(1, 9, 1));
        pipeline.addLast("handler", new HettyHttpHandler(hostQueueWriterMapping, messageFactory));
        return pipeline;
    }
}
