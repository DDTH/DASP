package ddth.dasp.hetty.front;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.qnt.IQueueWriter;

public class HettyHttpHandler extends IdleStateAwareChannelHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(HettyHttpHandler.class);
    private HttpRequest currentRequest;
    private ByteArrayOutputStream currentRequestContent = new ByteArrayOutputStream(4096);

    private ConcurrentMap<String, IQueueWriter> hostQueueWriterMapping = new ConcurrentHashMap<String, IQueueWriter>();
    private IMessageFactory messageFactory;

    public HettyHttpHandler(Map<String, IQueueWriter> hostQueueWriterMapping,
            IMessageFactory messageFactory) {
        if (hostQueueWriterMapping != null) {
            this.hostQueueWriterMapping.putAll(hostQueueWriterMapping);
        }
        this.messageFactory = messageFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        HettyConnServer.ALL_CHANNELS.add(e.getChannel());
    }

    protected void validateHttpChunk(HttpChunk httpChunk) {
        if (currentRequest == null || currentRequest.getContent() == null) {
            throw new IllegalStateException("No chunk started!");
        }
    }

    private IQueueWriter lookupQueueWriter(String host) {
        IQueueWriter queueWriter = hostQueueWriterMapping.get(host);
        if (queueWriter == null) {
            queueWriter = hostQueueWriterMapping.get("*");
        }
        return queueWriter;
    }

    protected void handleRequest(HttpRequest httpRequest, byte[] requestContent, Channel userChannel)
            throws Exception {
        String uri = httpRequest.getUri();
        if (uri.startsWith("/status/") || uri.startsWith("/status?") || uri.equals("/status")) {
            StringBuilder content = new StringBuilder();
            content.append("Connections: " + HettyConnServer.ALL_CHANNELS.size());

            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer(content.toString(), CharsetUtil.UTF_8));
            userChannel.write(response).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.getChannel().close();
                }
            });
            return;
        }

        IRequest request = messageFactory.buildRequest(httpRequest, userChannel.getId(),
                requestContent);
        String host = request.getDomain();
        IQueueWriter queueWriter = lookupQueueWriter(host);
        if (queueWriter != null) {
            userChannel.setAttachment(request.getId());
            if (!queueWriter.writeToQueue(request.serialize())) {
                StringBuilder content = new StringBuilder();
                content.append("No request queue for [" + host + "], or queue is full!");
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.BAD_GATEWAY);
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.setContent(ChannelBuffers.copiedBuffer(content.toString(),
                        CharsetUtil.UTF_8));
                userChannel.write(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        future.getChannel().close();
                    }
                });
            }
        } else {
            StringBuilder content = new StringBuilder();
            content.append("Host [" + host + "] is not mapped!");
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer(content.toString(), CharsetUtil.UTF_8));
            userChannel.write(response).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.getChannel().close();
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        boolean processMessage = false;
        if (message instanceof HttpChunk) {
            validateHttpChunk((HttpChunk) message);

            HttpChunk httpChunk = (HttpChunk) message;
            currentRequestContent.write(httpChunk.getContent().array());
            ChannelBuffer compositeBuffer = ChannelBuffers.wrappedBuffer(
                    currentRequest.getContent(), httpChunk.getContent());
            currentRequest.setContent(compositeBuffer);
            processMessage = httpChunk.isLast();
        } else if (message instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) message;
            currentRequestContent.write(httpRequest.getContent().array());
            currentRequest = httpRequest;
            processMessage = !currentRequest.isChunked();
        }
        if (processMessage) {
            handleRequest(currentRequest, currentRequestContent.toByteArray(), e.getChannel());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
        ChannelFuture future = e.getFuture();
        if (future != null) {
            e.getFuture().cancel();
        }
        e.getChannel().close();
        if (LOGGER.isDebugEnabled()) {
            String msg = "Timeout [" + e.getState() + "]: " + e.getChannel();
            LOGGER.debug(msg);
        }
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
