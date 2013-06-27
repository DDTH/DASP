package ddth.dasp.hetty.utils;

import java.util.Map;

import org.jboss.netty.channel.ChannelLocal;

public class ChannelUtils {
    public final static ChannelLocal<Map<String, Object>> CHANNEL_INFO = new ChannelLocal<Map<String, Object>>(
            true);
}
