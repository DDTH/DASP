package ddth.dasp.hetty.mvc;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import ddth.dasp.framework.osgi.IServiceAutoRegister;
import ddth.dasp.framework.resource.IResourceLoader;
import ddth.dasp.hetty.IRequestActionHandler;
import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.message.protobuf.ResponseUtils;
import ddth.dasp.hetty.qnt.ITopicPublisher;

/**
 * An {@link IRequestActionHandler} to server static resources inside bundle.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 */
public class BundleStaticResourceActionHandler implements IRequestActionHandler,
        IServiceAutoRegister {

    private static Map<String, String> DEFAULT_MIMETYPES = new HashMap<String, String>();
    static {
        DEFAULT_MIMETYPES.put(".gif", "image/gif");
        DEFAULT_MIMETYPES.put(".jpg", "image/jpeg");
        DEFAULT_MIMETYPES.put(".jpeg", "image/jpeg");
        DEFAULT_MIMETYPES.put(".png", "image/png");
        DEFAULT_MIMETYPES.put(".css", "text/css");
        DEFAULT_MIMETYPES.put(".js", "text/javascript");

        DEFAULT_MIMETYPES.put(".htm", "text/html");
        DEFAULT_MIMETYPES.put(".html", "text/html");
        DEFAULT_MIMETYPES.put(".txt", "text/txt");
    }

    private IResourceLoader resourceLoader;
    private String module, action, prefix;
    private String defaultMimeType = "application/octet-stream";
    private Map<String, String> mimeTypes = Collections.unmodifiableMap(DEFAULT_MIMETYPES);
    private Properties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return IRequestActionHandler.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setDefaultMimeType(String defaultMimeType) {
        this.defaultMimeType = defaultMimeType;
    }

    protected String getDefaultMimeType() {
        return defaultMimeType;
    }

    public Map<String, String> getMimeTypes() {
        return mimeTypes;
    }

    public void setMimeTypes(Map<String, String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public void setResourceLoader(IResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    protected String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    protected String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    protected String buildPath(String path) {
        return !StringUtils.isBlank(prefix) ? prefix + path : path;
    }

    protected String detectMimeType(String path) {
        for (Entry<String, String> mimeType : mimeTypes.entrySet()) {
            if (path.endsWith(mimeType.getKey())) {
                return mimeType.getValue();
            }
        }
        return defaultMimeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleRequest(IRequest request, ITopicPublisher topicPublisher, String topicName)
            throws Exception {
        String path = request.getPath();
        if (module != null && path.startsWith("/" + module)) {
            path = path.substring(module.length() + 1);
        }
        if (action != null && path.startsWith("/" + action)) {
            path = path.substring(action.length() + 1);
        }
        path = path.replaceAll("^\\/+", "");
        path = buildPath(path);
        IResponse response = null;
        if (resourceLoader.resourceExists(path)) {
            byte[] resourceContent = resourceLoader.loadResourceAsBinary(path);
            String etag = DigestUtils.md5Hex(resourceContent);
            String headerIfNoneMatch = request.getHeader("If-None-Match");
            if (headerIfNoneMatch != null && headerIfNoneMatch.equals(etag)) {
                response = ResponseUtils.response304(request);
            } else {
                String resourceMimeType = detectMimeType(path);
                response = ResponseUtils.response200(request, resourceContent, resourceMimeType
                        + "; charset=utf-8");
            }
            // cache headers
            response.addHeader("ETag", etag).addHeader("Cache-control", "private")
                    .addHeader("Max-Age", 1 * 3600);
            long resourceTimestamp = resourceLoader.getLastModified(path);
            if (resourceTimestamp > 0) {
                response.addHeader("Last-Modified", new Date(resourceTimestamp));
                // 1 hour in millisecs
                response.addHeader("Expires", new Date(System.currentTimeMillis() + 1 * 3600000L));
            }
        } else {
            response = ResponseUtils.response404(request);
        }
        topicPublisher.publish(topicName, response);
    }
}
