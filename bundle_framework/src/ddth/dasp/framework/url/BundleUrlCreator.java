package ddth.dasp.framework.url;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ServletContextAware;

import ddth.dasp.common.DaspGlobal;

public class BundleUrlCreator implements IUrlCreator, ServletContextAware {

    private String urlSuffix;
    private ServletContext servletContext;
    private HttpServletResponse httpResponse;

    /**
     * {@inheritDoc}
     */
    @Override
    public BundleUrlCreator clone() throws CloneNotSupportedException {
        BundleUrlCreator urlCreator = (BundleUrlCreator) super.clone();
        urlCreator.servletContext = servletContext;
        urlCreator.urlSuffix = urlSuffix;
        urlCreator.httpResponse = httpResponse;
        return urlCreator;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] virtualParams, Map<String, Object> urlParams) {
        return createUrl(httpResponse, virtualParams, urlParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(HttpServletResponse response, String[] virtualParams,
            Map<String, Object> urlParams) {
        return createUrl(response, virtualParams, urlParams, urlSuffix, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] virtualParams, Map<String, Object> urlParams,
            String urlSuffix, boolean absoluteUrl, boolean forceHttps) {
        return createUrl(httpResponse, virtualParams, urlParams, urlSuffix, absoluteUrl, forceHttps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(HttpServletResponse response, String[] virtualParams,
            Map<String, Object> urlParams, String urlSuffix, boolean absoluteUrl, boolean forceHttps) {
        ServletContext servletContext = getServletContext();
        StringBuilder url = new StringBuilder(servletContext.getContextPath());

        for (String param : virtualParams) {
            url.append("/").append(param);
        }
        if (!StringUtils.isBlank(urlSuffix)) {
            url.append(urlSuffix);
        }

        if (urlParams != null && urlParams.size() > 0) {
            url.append("?");
            for (Entry<String, ?> entry : urlParams.entrySet()) {
                url.append(entry.getKey());
                url.append("=");
                url.append(entry.getValue());
                url.append("&");
            }
            url.setLength(url.length() - 1);
        }

        if (absoluteUrl || forceHttps) {
            // TODO
        }

        return response != null ? response.encodeURL(url.toString()) : url.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    protected ServletContext getServletContext() {
        return servletContext != null ? servletContext : DaspGlobal.getServletContext();
    }
}
