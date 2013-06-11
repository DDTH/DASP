package ddth.dasp.hetty.message.protobuf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;

import ddth.dasp.hetty.message.ICookie;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.message.protobuf.HettyProtoBuf.NameValue;

public class ProtoBufResponse implements IResponse {

    private boolean dirty = false;
    // private List<ICookie> cookies;
    private Map<String, String> headers;
    private HettyProtoBuf.ResponseOrBuilder response;

    public ProtoBufResponse(HettyProtoBuf.Response response) {
        this.response = response;
    }

    public ProtoBufResponse(HettyProtoBuf.Response.Builder responseBuilder) {
        this.response = responseBuilder;
    }

    private HettyProtoBuf.Response.Builder ensureBuilder() {
        if (response instanceof HettyProtoBuf.Response) {
            response = HettyProtoBuf.Response.newBuilder((HettyProtoBuf.Response) response);
        }
        return (HettyProtoBuf.Response.Builder) response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize() {
        refresh();
        if (response instanceof HettyProtoBuf.Response) {
            return ((HettyProtoBuf.Response) response).toByteArray();
        }
        if (response instanceof HettyProtoBuf.Response.Builder) {
            HettyProtoBuf.Response.Builder builder = ((HettyProtoBuf.Response.Builder) response)
                    .clone();
            return builder.build().toByteArray();
        }
        return null;
    }

    private void refresh() {
        if (dirty) {
            if (headers != null && headers.size() > 0) {
                HettyProtoBuf.Response.Builder builder = ensureBuilder();
                builder.clearHeaders();
                for (Entry<String, String> header : headers.entrySet()) {
                    HettyProtoBuf.NameValue nameValue = HettyProtoBuf.NameValue.newBuilder()
                            .setName(header.getKey()).setValue(header.getValue()).build();
                    builder.addHeaders(nameValue);
                }
            }
        }
        dirty = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestId() {
        return response.hasRequestId() ? response.getRequestId() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRequestTimestampNano() {
        return response.hasRequestTimestampNano() ? response.getRequestTimestampNano() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChannelId() {
        return response.hasChannelId() ? response.getChannelId() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatus() {
        return response.hasStatus() ? response.getStatus() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse setStatus(int status) {
        HettyProtoBuf.Response.Builder builder = ensureBuilder();
        builder.setStatus(status);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse addCookie(ICookie cookie) {
        HettyProtoBuf.Cookie.Builder cookieBuilder = HettyProtoBuf.Cookie.newBuilder();
        cookieBuilder.setDomain(cookie.getDomain());
        cookieBuilder.setMaxAge(cookie.getMaxAge());
        cookieBuilder.setName(cookie.getName());
        cookieBuilder.setPath(cookie.getPath());
        cookieBuilder.setPort(cookie.getPort());
        cookieBuilder.setValue(cookie.getValue());
        HettyProtoBuf.Response.Builder builder = ensureBuilder();
        builder.addCookies(cookieBuilder.build());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICookie[] getCookies() {
        // if (cookies == null) {
        List<ICookie> cookies = new ArrayList<ICookie>();
        List<HettyProtoBuf.Cookie> cookieList = response.getCookiesList();
        if (cookieList != null) {
            for (HettyProtoBuf.Cookie cookie : cookieList) {
                cookies.add(new ProtoBufCookie(cookie));
            }
        }
        // }
        return cookies.toArray(new ICookie[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse addHeader(String name, String value) {
        Map<String, String> headers = getHeaders();
        headers.put(name, value);
        dirty = true;
        return this;
        // HettyProtoBuf.NameValue header =
        // HettyProtoBuf.NameValue.newBuilder().setName(name)
        // .setValue(value).build();
        // HettyProtoBuf.Response.Builder builder = ensureBuilder();
        // builder.addHeaders(header);
        // return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse addHeader(String name, Date value) {
        Map<String, String> headers = getHeaders();
        headers.put(name, ResponseUtils.DF_HEADER.format(value));
        dirty = true;
        return this;
        // HettyProtoBuf.NameValue header =
        // HettyProtoBuf.NameValue.newBuilder().setName(name)
        // .setValue(ResponseUtils.DF_HEADER.format(value)).build();
        // HettyProtoBuf.Response.Builder builder = ensureBuilder();
        // builder.addHeaders(header);
        // return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse addHeader(String name, int value) {
        Map<String, String> headers = getHeaders();
        headers.put(name, String.valueOf(value));
        dirty = true;
        return this;
        // HettyProtoBuf.NameValue header =
        // HettyProtoBuf.NameValue.newBuilder().setName(name)
        // .setValue(String.valueOf(value)).build();
        // HettyProtoBuf.Response.Builder builder = ensureBuilder();
        // builder.addHeaders(header);
        // return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse addHeader(String name, long value) {
        Map<String, String> headers = getHeaders();
        headers.put(name, String.valueOf(value));
        dirty = true;
        return this;
        // HettyProtoBuf.NameValue header =
        // HettyProtoBuf.NameValue.newBuilder().setName(name)
        // .setValue(String.valueOf(value)).build();
        // HettyProtoBuf.Response.Builder builder = ensureBuilder();
        // builder.addHeaders(header);
        // return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<String, String>();
            List<HettyProtoBuf.NameValue> headersList = response.getHeadersList();
            if (headersList != null) {
                for (NameValue header : headersList) {
                    headers.put(header.getName(), header.getValue());
                }
            }
        }
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getContent() {
        return response.hasContent() ? response.getContent().toByteArray() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse setContent(byte[] content) {
        ByteString byteStr = ByteString.copyFrom(content);
        addHeader("Content-Length", byteStr.size());
        HettyProtoBuf.Response.Builder builder = ensureBuilder();
        builder.setContent(byteStr);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtoBufResponse setContent(String content) {
        ByteString byteStr = ByteString.copyFromUtf8(content);
        addHeader("Content-Length", byteStr.size());
        HettyProtoBuf.Response.Builder builder = ensureBuilder();
        builder.setContent(byteStr);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChunk() {
        return response.hasIsChunk() ? response.getIsChunk() : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChunkSequence() {
        return response.hasChunkSeq() ? response.getChunkSeq() : 0;
    }
}
