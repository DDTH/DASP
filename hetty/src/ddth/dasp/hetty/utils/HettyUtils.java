package ddth.dasp.hetty.utils;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelLocal;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import ddth.dasp.hetty.front.HettyConnServer;

public class HettyUtils {
    public final static ChannelLocal<Map<String, Object>> CHANNEL_INFO = new ChannelLocal<Map<String, Object>>(
            true);
    public final static ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(
            HettyConnServer.class.getCanonicalName());

    public static void responseText(Channel channel, HttpResponseStatus status, String msg) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
        channel.write(response).addListener(ChannelFutureListener.CLOSE);
    }
}
