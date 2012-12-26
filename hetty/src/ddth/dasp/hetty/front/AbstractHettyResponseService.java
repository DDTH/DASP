package ddth.dasp.hetty.front;

import java.util.List;

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

import ddth.dasp.hetty.message.HettyProtoBuf;

public abstract class AbstractHettyResponseService implements IHettyResponseService {
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeResponse(HettyProtoBuf.Response responseProtobuf) {
        Integer channelId = responseProtobuf.getChannelId();
        Channel channel = HettyConnServer.ALL_CHANNELS.find(channelId);
        if (channel != null) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(responseProtobuf.getStatus());
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                    status != null ? status : HttpResponseStatus.OK);

            // headers
            for (HettyProtoBuf.NameValue header : responseProtobuf.getHeadersList()) {
                response.addHeader(header.getName(), header.getValue());
            }

            // cookies
            List<HettyProtoBuf.Cookie> cookieList = responseProtobuf.getCookiesList();
            if (cookieList.size() > 0) {
                CookieEncoder cookieEncoder = new CookieEncoder(false);
                for (HettyProtoBuf.Cookie cookie : cookieList) {
                    DefaultCookie nettyCookie = new DefaultCookie(cookie.getName(),
                            cookie.getValue());
                    if (!StringUtils.isBlank(cookie.getDomain())) {
                        nettyCookie.setDomain(cookie.getDomain());
                    }
                    if (!StringUtils.isBlank(cookie.getPath())) {
                        nettyCookie.setDomain(cookie.getPath());
                    }
                    if (cookie.hasPort() && cookie.getPort() > 0) {
                        nettyCookie.setPorts(cookie.getPort());
                    }
                    if (cookie.hasMaxAge()) {
                        nettyCookie.setMaxAge(cookie.getMaxAge());
                    }
                    cookieEncoder.addCookie(nettyCookie);
                }
                response.setHeader("Cookie", cookieEncoder.encode());
            }

            // content
            if (responseProtobuf.hasContent() && responseProtobuf.getContent() != null) {
                response.setContent(ChannelBuffers.copiedBuffer(responseProtobuf.getContent()
                        .toByteArray()));
            } else {
                response.setContent(ChannelBuffers.copiedBuffer("", CharsetUtil.UTF_8));
            }

            channel.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
