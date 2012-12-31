package ddth.dasp.hetty;

import java.util.Map;

public interface IUrlCreator {
    /**
     * Creates a relative URL with default options.
     * 
     * @param pathParams
     * @param urlParams
     * @return
     */
    public String createUrl(String[] pathParams, Map<String, Object> urlParams);

    /**
     * Creates an absolute URL with default schema.
     * 
     * @param pathParams
     * @param urlParams
     * @param host
     * @return
     */
    public String createUrl(String[] pathParams, Map<String, Object> urlParams, String host);

    /**
     * Creates an absolute URL with full options.
     * 
     * @param pathParams
     * @param urlParams
     * @param host
     * @param scheme
     * @return
     */
    public String createUrl(String[] pathParams, Map<String, Object> urlParams, String host,
            String scheme);
}
