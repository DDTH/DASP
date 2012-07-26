package ddth.dasp.framework.url;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface IUrlCreator extends Cloneable {

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
     * @param response
     * @param virtualParams
     * @param urlParams
     * @param urlSuffix
     * @param absoluteUrl
     * @param forceHttps
     * @return
     */
    public String createUrl(HttpServletResponse response, String[] virtualParams,
            Map<String, Object> urlParams, String urlSuffix, boolean absoluteUrl, boolean forceHttps);

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    public IUrlCreator clone();
}
