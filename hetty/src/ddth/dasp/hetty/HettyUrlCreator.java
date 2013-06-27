package ddth.dasp.hetty;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class HettyUrlCreator implements IUrlCreator {

    private String urlSuffix;

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] pathParams, Map<String, Object> urlParams) {
        return createUrl(pathParams, urlParams, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] pathParams, Map<String, Object> urlParams, String host) {
        return createUrl(pathParams, urlParams, host, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] pathParams, Map<String, Object> urlParams, String host,
            String scheme) {
        StringBuilder sb = new StringBuilder();
        // host & scheme
        if (!StringUtils.isBlank(host)) {
            if (!StringUtils.isBlank(scheme)) {
                sb.append(scheme.replaceAll("\\W+$", "")).append(":");
            }
            sb.append("//").append(host.replaceAll("^\\/+", "").replaceAll("\\/+$", ""));
        }

        // path params
        if (pathParams != null && pathParams.length > 0) {
            for (String param : pathParams) {
                sb.append("/").append(param);
            }
            if (!StringUtils.isBlank(urlSuffix)) {
                sb.append(urlSuffix);
            }
        } else {
            sb.append("/");
        }

        // url params
        if (urlParams != null && urlParams.size() > 0) {
            sb.append("?");
            for (Entry<String, Object> entry : urlParams.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        return sb.toString();
    }
}
