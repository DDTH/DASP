package ddth.dasp.hetty.message.protobuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import ddth.dasp.hetty.message.ICookie;
import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.protobuf.HettyProtoBuf.NameValue;

public class ProtoBufRequest implements IRequest {

    private HettyProtoBuf.RequestOrBuilder request;

    public ProtoBufRequest(HettyProtoBuf.Request request) {
        this.request = request;
    }

    public ProtoBufRequest(HettyProtoBuf.Request.Builder requestBuilder) {
        this.request = requestBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize() {
        if (request instanceof HettyProtoBuf.Request) {
            return ((HettyProtoBuf.Request) request).toByteArray();
        }
        if (request instanceof HettyProtoBuf.Request.Builder) {
            HettyProtoBuf.Request.Builder builder = ((HettyProtoBuf.Request.Builder) request)
                    .clone();
            return builder.build().toByteArray();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return request.hasId() ? request.getId() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseTopic() {
        return request.hasResponseTopic() ? request.getResponseTopic() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChannelId() {
        return request.hasChannelId() ? request.getChannelId() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimestamp() {
        return request.hasTimestamp() ? request.getTimestamp() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMethod() {
        return request.hasMethod() ? request.getMethod() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUri() {
        return request.hasUri() ? request.getUri() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDomain() {
        return request.hasDomain() ? request.getDomain() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return request.hasPort() ? request.getPort() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return request.hasPath() ? request.getPath() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getUrlParams() {
        Map<String, String> urlParams = new HashMap<String, String>();
        List<HettyProtoBuf.NameValue> urlParamsList = request.getUrlParamsList();
        if (urlParamsList != null) {
            for (NameValue param : urlParamsList) {
                urlParams.put(param.getName(), param.getValue());
            }
        }
        return urlParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getPathParams() {
        List<String> pathParams = request.getPathParamsList();
        return pathParams != null ? pathParams.toArray(ArrayUtils.EMPTY_STRING_ARRAY)
                : ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICookie[] getCookies() {
        List<ICookie> cookies = new ArrayList<ICookie>();
        List<HettyProtoBuf.Cookie> cookieList = request.getCookiesList();
        if (cookieList != null) {
            for (HettyProtoBuf.Cookie cookie : cookieList) {
                cookies.add(new ProtoBufCookie(cookie));
            }
        }
        return cookies.toArray(new ICookie[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(String name) {
        List<HettyProtoBuf.NameValue> headersList = request.getHeadersList();
        if (headersList != null) {
            for (NameValue header : headersList) {
                if (name.equals(header.getName())) {
                    return header.getValue();
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        List<HettyProtoBuf.NameValue> headersList = request.getHeadersList();
        if (headersList != null) {
            for (NameValue header : headersList) {
                headers.put(header.getName(), header.getValue());
            }
        }
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent() {
        return request.hasContent() ? request.getContent().toByteArray() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChunk() {
        return request.hasIsChunk() ? request.getIsChunk() : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChunkSequence() {
        return request.hasChunkSeq() ? request.getChunkSeq() : 0;
    }
}
