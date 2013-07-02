package ddth.dasp.hetty.front;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.hetty.HettyConstants;
import ddth.dasp.hetty.message.ICookie;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.utils.HettyUtils;

public abstract class AbstractHettyResponseService implements IHettyResponseService {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(AbstractHettyResponseService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeResponse(IResponse response) {
        Integer channelId = response.getChannelId();
        Channel channel = HettyUtils.ALL_CHANNELS.find(channelId);
        Object requestId = HettyUtils.getChannelLocalAttribute(channel,
                HettyConstants.CHA_REQUEST_ID);
        if (channel != null && response.getRequestId().equals(requestId)) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(response.getStatus());
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                    status != null ? status : HttpResponseStatus.OK);

            // headers
            for (Entry<String, String> header : response.getHeaders().entrySet()) {
                httpResponse.addHeader(header.getKey(), header.getValue());
            }

            // cookies
            ICookie[] cookies = response.getCookies();
            if (cookies.length > 0) {
                CookieEncoder cookieEncoder = new CookieEncoder(false);
                for (ICookie cookie : cookies) {
                    DefaultCookie nettyCookie = new DefaultCookie(cookie.getName(),
                            cookie.getValue());
                    if (!StringUtils.isBlank(cookie.getDomain())) {
                        nettyCookie.setDomain(cookie.getDomain());
                    }
                    if (!StringUtils.isBlank(cookie.getPath())) {
                        nettyCookie.setDomain(cookie.getPath());
                    }
                    if (cookie.getPort() > 0) {
                        nettyCookie.setPorts(cookie.getPort());
                    }
                    if (cookie.getMaxAge() > 0) {
                        nettyCookie.setMaxAge(cookie.getMaxAge());
                    }
                    cookieEncoder.addCookie(nettyCookie);
                }
                httpResponse.setHeader("Cookie", cookieEncoder.encode());
            }

            // content
            if (response.getContent() != null) {
                httpResponse.setContent(ChannelBuffers.copiedBuffer(response.getContent()));
            } else {
                httpResponse.setContent(ChannelBuffers.copiedBuffer("", CharsetUtil.UTF_8));
            }

            channel.write(httpResponse).addListener(ChannelFutureListener.CLOSE);

            if (LOGGER.isDebugEnabled()) {
                long timestamp = System.nanoTime();
                StringBuilder logMsg = new StringBuilder(response.getRequestId()).append("/")
                        .append(response.getStatus()).append("/")
                        .append((timestamp - response.getRequestTimestampNano()) / 1E6)
                        .append(" ms");
                LOGGER.debug(logMsg.toString());
            }
        }
    }
}
