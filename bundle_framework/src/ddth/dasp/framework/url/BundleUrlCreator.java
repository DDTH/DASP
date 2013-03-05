package ddth.dasp.framework.url;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ServletContextAware;

import ddth.dasp.common.DaspGlobal;

public class BundleUrlCreator implements IUrlCreator, ServletContextAware {

    private String urlSuffix;
    private ServletContext servletContext;
    private HttpServletResponse httpResponse;
    private HttpServletRequest httpRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    public BundleUrlCreator clone() {
        BundleUrlCreator urlCreator;
        try {
            urlCreator = (BundleUrlCreator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        urlCreator.servletContext = servletContext;
        urlCreator.urlSuffix = urlSuffix;
        urlCreator.httpResponse = httpResponse;
        urlCreator.httpRequest = httpRequest;
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

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
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
        return createUrl(null, response, virtualParams, urlParams, urlSuffix, false, false,
                DEFAULT_HTTP_PORT, DEFAULT_HTTPS_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(String[] virtualParams, Map<String, Object> urlParams,
            String urlSuffix, boolean absoluteUrl, boolean forceHttps) {
        return createUrl(httpRequest, httpResponse, virtualParams, urlParams, urlSuffix,
                absoluteUrl, forceHttps, DEFAULT_HTTP_PORT, DEFAULT_HTTPS_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams, String urlSuffix,
            boolean absoluteUrl, boolean forceHttps, int httpPort, int httpsPort) {
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

        if (request != null && (absoluteUrl || forceHttps)) {
            String domain = request.getServerName();
            int port = request.getServerPort();
            String scheme = request.getScheme();
            if (!forceHttps) {
                if (port != DEFAULT_HTTP_PORT && port != DEFAULT_HTTPS_PORT) {
                    url.insert(0, ":" + port);
                }
                url.insert(0, scheme + "://" + domain);
            } else {
                if (httpsPort != DEFAULT_HTTPS_PORT) {
                    url.insert(0, ":" + httpsPort);
                }
                url.insert(0, "https://" + domain);
            }
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
