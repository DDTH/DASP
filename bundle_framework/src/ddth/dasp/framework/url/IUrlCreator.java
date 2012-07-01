package ddth.dasp.framework.url;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUrlCreator {
    /**
     * Creates a URL with default options.
     * 
     * @param request
     * @param response
     * @param virtualParams
     * @param urlParams
     * @return
     */
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams);

    /**
     * Creates a URL with full options.
     * 
     * @param request
     * @param response
     * @param virtualParams
     * @param urlParams
     * @param urlSuffix
     * @param absoluteUrl
     * @param forceHttps
     * @return
     */
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams, String urlSuffix,
            boolean absoluteUrl, boolean forceHttps);
}
