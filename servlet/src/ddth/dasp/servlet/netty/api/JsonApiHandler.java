package ddth.dasp.servlet.netty.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.api.IApiGroupHandler;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.utils.ApiUtils;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.servlet.netty.AbstractHttpHandler;

public class JsonApiHandler extends AbstractHttpHandler {

    private final static String URI_PREFIX = "/api";
    private static AtomicLong counter = new AtomicLong();
    private static Logger LOGGER = LoggerFactory.getLogger(JsonApiHandler.class);

    private ChannelFuture responseError(Channel channel, HttpResponseStatus status, String message) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(message, CharsetUtil.UTF_8));
        ChannelFuture future = channel.write(response);
        return future;
    }

    private ChannelFuture responseApiCall(Channel channel, String uri) {
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
                    Map<Object, Object> res = ApiUtils.createApiResult(404, "No handler for ["
                            + moduleName + "/" + functionName + "]!");
                    result = res;
                }
            }
        } catch (Exception ex) {
            Map<Object, Object> res = ApiUtils.createApiResult(500, ex.getMessage());
            result = res;
        }

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(JsonUtils.toJson(result), CharsetUtil.UTF_8));
        ChannelFuture future = channel.write(response);
        return future;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleRequest(HttpRequest request, Channel userChannel) throws Exception {
        counter.incrementAndGet();
        try {
            String uri = request.getUri();
            String contextPath = DaspGlobal.getServletContext().getContextPath();
            if (uri.startsWith(contextPath)) {
                uri = uri.substring(contextPath.length());
            }
            ChannelFuture future;
            if (!uri.startsWith(URI_PREFIX)) {
                future = responseError(userChannel, HttpResponseStatus.BAD_REQUEST,
                        "Request must starts with '/api'!");
            } else {
                future = responseApiCall(userChannel, uri.substring(URI_PREFIX.length()));
            }
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            counter.decrementAndGet();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable t = e.getCause();
        LOGGER.error(t.getMessage(), t);
        e.getChannel().close();
        e.getFuture().cancel();
    }
}
