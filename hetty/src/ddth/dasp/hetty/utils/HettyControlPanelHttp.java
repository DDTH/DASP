package ddth.dasp.hetty.utils;

import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.config.IConfigDao;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.hetty.front.HettyConnServer;
import ddth.dasp.hetty.message.IRequest;

public class HettyControlPanelHttp {

    public static void handleRequest(IRequest request, byte[] requestContent, Channel userChannel) {
        String[] pathParams = request.getPathParams();
        String module = pathParams != null && pathParams.length > 1 ? pathParams[1] : null;

        if ("mapping".equalsIgnoreCase(module)) {
            handle_mapping(request, requestContent, userChannel);
        } else {
            HettyUtils.responseText(userChannel, HttpResponseStatus.OK, "Connections: "
                    + HettyUtils.ALL_CHANNELS.size());
        }
    }

    public static final String MODULE = "hetty";
    public static final String CONFIG_KEY = "mapping";
    private static final String PARAM_HOST = "host";
    private static final String PARAM_QUEUE = "queue";

    @SuppressWarnings("unchecked")
    private static void handle_mapping(IRequest request, byte[] requestContent, Channel userChannel) {
        String[] pathParams = request.getPathParams();
        String action = pathParams != null && pathParams.length > 2 ? pathParams[2] : null;
        if ("save".equalsIgnoreCase(action)) {
            Map<String, Object> mapping = HettyConnServer.getHostQueueNameMapping();
            IConfigDao configDao = getOsgiService(IConfigDao.class);
            configDao.setConfig(MODULE, CONFIG_KEY, mapping);
        } else if ("load".equalsIgnoreCase(action)) {
            IConfigDao configDao = getOsgiService(IConfigDao.class);
            HettyConnServer.setHostQueueNameMapping((Map<String, Object>) configDao.getConfig(
                    MODULE, CONFIG_KEY));
        } else if ("delete".equalsIgnoreCase(action)) {
            Map<String, String> uriParams = request.getUrlParams();
            String host = uriParams.get(PARAM_HOST);
            HettyConnServer.deleteHostQueueNameMapping(host);
        } else if ("set".equalsIgnoreCase(action)) {
            Map<String, String> uriParams = request.getUrlParams();
            String host = uriParams.get(PARAM_HOST);
            String queue = uriParams.get(PARAM_QUEUE);
            HettyConnServer.addHostQueueNameMapping(host, queue);
        }
        Map<String, Object> mapping = HettyConnServer.getHostQueueNameMapping();
        String content = HettyUtils
                .loadContentInClasspath("/ddth/dasp/hetty/utils/hcp_mapping.tpl");
        content = content.replace("${mapping}", JsonUtils.toJson(mapping));
        HettyUtils.responseHtml(userChannel, HttpResponseStatus.OK, content);
    }

    private static <T> T getOsgiService(Class<T> clazz) {
        return DaspGlobal.getOsgiBootstrap().getService(clazz);
    }
}
