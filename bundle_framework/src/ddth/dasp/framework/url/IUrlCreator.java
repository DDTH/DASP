package ddth.dasp.framework.url;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUrlCreator extends Cloneable {

    public final static int DEFAULT_HTTP_PORT = 80;
    public final static int DEFAULT_HTTPS_PORT = 443;

    /**
     * Creates a URL with default options.
     * 
     * @param virtualParams
     * @param urlParams
     * @return
     */
    public String createUrl(String[] virtualParams, Map<String, Object> urlParams);

    /**
     * Creates a URL with default options.
     * 
     * @param response
     * @param virtualParams
     * @param urlParams
     * @return
     */
    public String createUrl(HttpServletResponse response, String[] virtualParams,
            Map<String, Object> urlParams);

    /**
     * Creates a URL with full options.
     * 
     * @param virtualParams
     * @param urlParams
     * @param urlSuffix
     * @param absoluteUrl
     * @param forceHttps
     * @return
     */
    public String createUrl(String[] virtualParams, Map<String, Object> urlParams,
            String urlSuffix, boolean absoluteUrl, boolean forceHttps);

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
     * @param httpPort
     * @param httpsPort
     * @return
     */
    public String createUrl(HttpServletRequest request, HttpServletResponse response,
            String[] virtualParams, Map<String, Object> urlParams, String urlSuffix,
            boolean absoluteUrl, boolean forceHttps, int httpPort, int httpsPort);

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    public IUrlCreator clone();
}
