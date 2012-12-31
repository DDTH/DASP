package ddth.dasp.hetty.mvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import ddth.dasp.framework.osgi.IServiceAutoRegister;
import ddth.dasp.framework.resource.IResourceLoader;
import ddth.dasp.hetty.IRequestActionHandler;
import ddth.dasp.hetty.message.HettyProtoBuf;
import ddth.dasp.hetty.message.ResponseUtils;
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
    public void handleRequest(HettyProtoBuf.Request request, ITopicPublisher topicPublisher)
            throws Exception {
        String path = request.getPath();
        if (module != null && path.startsWith("/" + module)) {
            path = path.substring(module.length() + 1);
        }
        if (action != null && path.startsWith("/" + action)) {
            path = path.substring(action.length() + 1);
        }
        if (resourceLoader.resourceExists(path)) {
            byte[] resourceContent = resourceLoader.loadResourceAsBinary(path);
            String resourceMimeType = detectMimeType(path);
            HettyProtoBuf.Response response = ResponseUtils.response200(request, resourceContent,
                    resourceMimeType + "; charset=utf-8");
            topicPublisher.publishToTopic(response);
        } else {
            HettyProtoBuf.Response response = ResponseUtils.response404(request);
            topicPublisher.publishToTopic(response);
        }
    }
}
