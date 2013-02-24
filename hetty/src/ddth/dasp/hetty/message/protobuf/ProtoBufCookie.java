package ddth.dasp.hetty.message.protobuf;

import ddth.dasp.hetty.message.ICookie;

public class ProtoBufCookie implements ICookie {

    private HettyProtoBuf.CookieOrBuilder cookie;

    public ProtoBufCookie(HettyProtoBuf.Cookie cookie) {
        this.cookie = cookie;
    }

    public ProtoBufCookie(HettyProtoBuf.Cookie.Builder cookieBuilder) {
        this.cookie = cookieBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return cookie.hasName() ? cookie.getName() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return cookie.hasValue() ? cookie.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDomain() {
        return cookie.hasDomain() ? cookie.getDomain() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return cookie.hasPort() ? cookie.getPort() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return cookie.hasPath() ? cookie.getPath() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxAge() {
        return cookie.hasMaxAge() ? cookie.getMaxAge() : 0;
    }
}
