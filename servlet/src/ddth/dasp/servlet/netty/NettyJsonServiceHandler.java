package ddth.dasp.servlet.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.api.IApiGroupHandler;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.JsonUtils;

public class NettyJsonServiceHandler extends SimpleChannelUpstreamHandler {

    private final static String URI_PREFIX = "/api";

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpRequest request = (HttpRequest) e.getMessage();
        writeResponse(e, request);
    }

    private static AtomicLong counter = new AtomicLong();
    private static Random rand = new Random(System.currentTimeMillis());

    private void writeResponse(MessageEvent e, HttpRequest request) {
        String result = "";
        counter.incrementAndGet();
        try {
            int value = counter.intValue();
            System.out.println(value + ":" + request);
            Thread.sleep(Math.abs(rand.nextLong() % 5000));
            value = counter.intValue();
            System.out.println(value + ":" + request);
            result = value + "";
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            counter.decrementAndGet();
        }
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(JsonUtils.toJson(result), CharsetUtil.UTF_8));
        ChannelFuture future = e.getChannel().write(response);
        future.addListener(ChannelFutureListener.CLOSE);

        // String uri = request.getUri();
        // String contextPath = DaspGlobal.getServletContext().getContextPath();
        // if (uri.startsWith(contextPath)) {
        // uri = uri.substring(contextPath.length());
        // }
        //
        // ChannelFuture future;
        // if (!uri.startsWith(URI_PREFIX)) {
        // future = responseError(e, HttpResponseStatus.BAD_REQUEST,
        // "Request must starts with '/api'!");
        // } else {
        // future = responseApiCall(e, uri.substring(URI_PREFIX.length()));
        // }
        // future.addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture responseApiCall(MessageEvent e, String uri) {
        String[] tokens = uri.replaceAll("^\\/+", "").replaceAll("^\\/+", "").split("\\/");
        String moduleName = tokens.length > 0 ? tokens[0] : null;
        String functionName = tokens.length > 1 ? tokens[1] : null;
        String authKey = tokens.length > 2 ? tokens[2] : null;
        String jsonEncodedInput = null;

        Object result;
        try {
            IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
            Map<String, String> filter = new HashMap<String, String>();
            filter.put(IApiHandler.PROP_MODULE, moduleName);
            filter.put(IApiHandler.PROP_API, functionName);
            IApiHandler apiHandler = osgiBootstrap.getService(IApiHandler.class, filter);
            if (apiHandler != null) {
                Object params = JsonUtils.fromJson(jsonEncodedInput);
                result = apiHandler.callApi(params, authKey);
            } else {
                filter.remove(IApiHandler.PROP_API);
                IApiGroupHandler apiGroupHandler = osgiBootstrap.getService(IApiGroupHandler.class,
                        filter);
                if (apiGroupHandler != null) {
                    Object params = JsonUtils.fromJson(jsonEncodedInput);
                    result = apiGroupHandler.handleApiCall(functionName, params, authKey);
                } else {
                    Map<Object, Object> res = new HashMap<Object, Object>();
                    res.put("status", 404);
                    res.put("message", "No handler for [" + moduleName + "/" + functionName + "]!");
                    result = res;
                }
            }
        } catch (Exception ex) {
            Map<Object, Object> res = new HashMap<Object, Object>();
            res.put("status", 500);
            res.put("message", ex.getMessage());
            result = res;
        }

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(JsonUtils.toJson(result), CharsetUtil.UTF_8));
        ChannelFuture future = e.getChannel().write(response);
        return future;
    }

    private ChannelFuture responseError(MessageEvent e, HttpResponseStatus status, String message) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(message, CharsetUtil.UTF_8));
        ChannelFuture future = e.getChannel().write(response);
        return future;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
