package ddth.dasp.hetty.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
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

import ddth.dasp.hetty.HettyConstants;
import ddth.dasp.hetty.front.HettyConnServer;

public class HettyUtils {
    public final static ChannelLocal<Map<String, Object>> CHANNEL_LOCAL = new ChannelLocal<Map<String, Object>>(
            true);
    public final static ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(
            HettyConnServer.class.getCanonicalName());

    public static boolean writeResponse(Channel channel, HttpResponse httpResponse) {
        if (channel.isOpen() && channel.isWritable()
                && getChannelLocalAttribute(channel, HettyConstants.CHA_HAS_REQUEST) != null) {
            httpResponse.addHeader("Server", HettyConstants.SERVER_SIGNATURE);
            channel.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
            removeChannelLocalAttribute(channel, HettyConstants.CHA_HAS_REQUEST);
            return true;
        }
        return false;
    }

    public static void responseText(Channel channel, HttpResponseStatus status, String msg) {
        if (channel.isOpen() && channel.isWritable()) {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
            writeResponse(channel, response);
        }
    }

    public static void responseHtml(Channel channel, HttpResponseStatus status, String msg) {
        if (channel.isOpen() && channel.isWritable()) {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
            response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
            response.setContent(ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
            writeResponse(channel, response);
        }
    }

    public static Map<String, Object> resetChannelLocalAttribute(Channel channel) {
        Map<String, Object> channelLocalData = CHANNEL_LOCAL.get(channel);
        if (channelLocalData != null) {
            channelLocalData.clear();
        }
        return channelLocalData;
    }

    public static boolean setChannelLocalAttribute(Channel channel, String key, Object value) {
        Map<String, Object> channelLocalData = channel != null ? CHANNEL_LOCAL.get(channel) : null;
        if (channelLocalData != null) {
            channelLocalData.put(key, value);
            return true;
        }
        return false;
    }

    public static Object getChannelLocalAttribute(Channel channel, String key) {
        Map<String, Object> channelLocalData = channel != null ? CHANNEL_LOCAL.get(channel) : null;
        return channelLocalData != null ? channelLocalData.get(key) : null;
    }

    public static boolean removeChannelLocalAttribute(Channel channel, String key) {
        Map<String, Object> channelLocalData = channel != null ? CHANNEL_LOCAL.get(channel) : null;
        return channelLocalData != null && channelLocalData.remove(key) != null;
    }

    private static Map<String, String> contentCache = new ConcurrentHashMap<String, String>();

    public static String loadContentInClasspath(String path) {
        String content = contentCache.get(path);
        if (content == null) {
            InputStream is = HettyUtils.class.getResourceAsStream(path);
            try {
                content = IOUtils.toString(is, "UTF-8");
                contentCache.put(path, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return content;
    }
}
