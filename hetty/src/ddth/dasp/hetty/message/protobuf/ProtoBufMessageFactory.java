package ddth.dasp.hetty.message.protobuf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.IResponse;

public class ProtoBufMessageFactory implements IMessageFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public IRequest buildRequest(HttpRequest httpRequest, int channelId, byte[] requestContent) {
        String uri = httpRequest.getUri();
        String requestId = IdGenerator.getInstance(IdGenerator.getMacAddr()).generateId64Ascii();

        HettyProtoBuf.Request.Builder requestProtoBuf = HettyProtoBuf.Request.newBuilder();

        // populate required fields
        requestProtoBuf.setTimestamp(System.nanoTime());
        requestProtoBuf.setId(requestId);
        requestProtoBuf.setResponseTopic(requestId);
        requestProtoBuf.setChannelId(channelId);
        requestProtoBuf.setMethod(httpRequest.getMethod().toString());
        requestProtoBuf.setUri(uri);
        // parse path parameters
        QueryStringDecoder qsd = new QueryStringDecoder(uri, CharsetUtil.UTF_8);
        String path = qsd.getPath();
        requestProtoBuf.setPath(path);
        String[] pathTokens = path.replaceAll("^\\/+", "").replaceAll("\\/+$", "").split("\\/");
        requestProtoBuf.addAllPathParams(Arrays.asList(pathTokens));
        // parse url parameters
        Map<String, List<String>> urlParams = qsd.getParameters();
        for (Entry<String, List<String>> urlParam : urlParams.entrySet()) {
            String name = urlParam.getKey();
            String value = urlParam.getValue().size() > 0 ? urlParam.getValue().get(0) : "";
            HettyProtoBuf.NameValue param = HettyProtoBuf.NameValue.newBuilder().setName(name)
                    .setValue(value).build();
            requestProtoBuf.addUrlParams(param);
        }
        // parse host & port
        String hostAndPort = httpRequest.getHeader("Host");
        String[] tokens = hostAndPort.split(":");
        requestProtoBuf.setDomain(tokens[0]);
        try {
            requestProtoBuf.setPort(Integer.parseInt(tokens[1]));
        } catch (Exception e) {
            requestProtoBuf.setPort(80);
        }
        // parse & populate cookies
        String strCookie = httpRequest.getHeader("Cookie");
        if (!StringUtils.isBlank(strCookie)) {
            Set<Cookie> cookies = new CookieDecoder().decode(strCookie);
            for (Cookie cookie : cookies) {
                HettyProtoBuf.Cookie.Builder cookieProtoBuf = HettyProtoBuf.Cookie.newBuilder();
                cookieProtoBuf.setName(cookie.getName());
                cookieProtoBuf.setValue(cookie.getValue());
                cookieProtoBuf.setDomain(cookie.getDomain() != null ? cookie.getDomain() : "");
                cookieProtoBuf.setPath(cookie.getPath() != null ? cookie.getPath() : "");
                cookieProtoBuf.setMaxAge(cookie.getMaxAge());
                requestProtoBuf.addCookies(cookieProtoBuf);
            }
        }
        // parse & populate other headers
        httpRequest.removeHeader("Host");
        httpRequest.removeHeader("Cookie");
        for (Entry<String, String> entry : httpRequest.getHeaders()) {
            HettyProtoBuf.NameValue.Builder headerProtoBuf = HettyProtoBuf.NameValue.newBuilder();
            headerProtoBuf.setName(entry.getKey());
            headerProtoBuf.setValue(entry.getValue());
            requestProtoBuf.addHeaders(headerProtoBuf);
        }
        // populate content
        requestProtoBuf.setContent(ByteString.copyFrom(requestContent));

        return new ProtoBufRequest(requestProtoBuf.build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRequest deserializeRequest(byte[] serializedData) {
        HettyProtoBuf.Request request;
        try {
            request = HettyProtoBuf.Request.parseFrom(serializedData);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return new ProtoBufRequest(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IResponse deserializeResponse(byte[] serializedData) {
        HettyProtoBuf.Response response;
        try {
            response = HettyProtoBuf.Response.parseFrom(serializedData);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return new ProtoBufResponse(response);
    }
}
