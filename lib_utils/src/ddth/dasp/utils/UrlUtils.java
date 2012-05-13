package ddth.wfp.utils;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

public class UrlUtils {
    /**
     * Creates a URL with default settings.
     * 
     * @param actions
     * @param params
     * @return
     */
    public static String createUrl(String[] actions, Map<String, ?> params) {
        ServletContext servletContext = ServletActionContext.getServletContext();
        StringBuilder url = new StringBuilder(servletContext.getContextPath());
        for (String action : actions) {
            url.append("/").append(action);
        }
        url.append(WfpConstants.URL_SUFFIX);

        if (params != null && params.size() > 0) {
            url.append("?");
            for (Entry<String, ?> entry : params.entrySet()) {
                url.append(entry.getKey());
                url.append("=");
                url.append(entry.getValue());
                url.append("&");
            }
            url.setLength(url.length() - 1);
        }

        HttpServletResponse servletResponse = ServletActionContext.getResponse();
        return servletResponse.encodeURL(url.toString());
    }
}
