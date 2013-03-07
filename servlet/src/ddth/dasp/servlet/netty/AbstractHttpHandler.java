package ddth.dasp.servlet.netty;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpHandler extends IdleStateAwareChannelHandler {
    public static ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("NettyHttpServer");
    public static AtomicLong COUNTER = new AtomicLong();

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractHttpHandler.class);

    private HttpRequest currentRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        COUNTER.incrementAndGet();
        try {
            Object message = e.getMessage();
            Channel userChannel = e.getChannel();
            boolean processMessage = false;
            if (message instanceof HttpChunk) {
                if (currentRequest == null) {
                    throw new IllegalStateException("No chunk started!");
                }
                ChannelBuffer channelBuffer = currentRequest.getContent();
                if (channelBuffer == null) {
                    throw new IllegalStateException("No chunk started!");
                }
                HttpChunk httpChunk = (HttpChunk) message;
                ChannelBuffer compositeBuffer = ChannelBuffers.wrappedBuffer(channelBuffer,
                        httpChunk.getContent());
                currentRequest.setContent(compositeBuffer);
                processMessage = httpChunk.isLast();
            } else if (message instanceof HttpRequest) {
                currentRequest = (HttpRequest) message;
                processMessage = !currentRequest.isChunked();
            }
            if (processMessage) {
                handleRequest(currentRequest, userChannel);
            }
        } finally {
            COUNTER.decrementAndGet();
        }
    }

    protected abstract void handleRequest(HttpRequest request, Channel userChannel)
            throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ALL_CHANNELS.add(e.getChannel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable t = e.getCause();
        LOGGER.error(t.getMessage(), t);
        ChannelFuture future = e.getFuture();
        if (future != null) {
            e.getFuture().cancel();
        }
        e.getChannel().close();
    }
}
