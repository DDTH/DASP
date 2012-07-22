package ddth.dasp.framework.url;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ddth.dasp.common.DaspGlobal;

public class BundleUrlCreator implements IUrlCreator {

    private String urlSuffix;

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams) {
        return createUrl(request, response, virtualParams, urlParams, urlSuffix, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams, String urlSuffix,
            boolean absoluteUrl, boolean forceHttps) {
        ServletContext servletContext = DaspGlobal.getServletContext();
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

        return response.encodeURL(url.toString());
    }
}
