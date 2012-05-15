package ddth.wfp.servlet.struts.model;

import org.apache.commons.lang.StringUtils;

import ddth.wfp.servlet.struts.action.BaseAction;

/**
 * Models the HTML page
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class PageModel extends BaseModel {
    public PageModel(BaseAction action) {
        super(action);
    }

    /**
     * Model: page's base href
     * 
     * @return
     */
    public String getBaseHref() {
        String requestedUri = getRequestUri();
        String requestedUrl = getRequestUrl();
        if (!StringUtils.isBlank(requestedUri)) {
            requestedUrl = requestedUrl.substring(0, requestedUrl.length() - requestedUri.length());
        }
        StringBuilder baserhref = new StringBuilder(requestedUrl);
        String contextPath = getServletContext().getContextPath();
        baserhref.append(contextPath);
        if (!contextPath.endsWith("/")) {
            baserhref.append("/");
        }
        // FIXME: hard code
        baserhref.append("skins/dojo/");
        return baserhref.toString();
    }

    public String getName() {
        return "[PAGE NAME]";
    }

    public String getTitle() {
        return "[PAGE TITLE]";
    }

    public String getKeywords() {
        return "[PAGE KEYWORDS]";
    }

    public String getDescription() {
        return "[PAGE DESCRIPTION]";
    }

    public String getCopyright() {
        return "[PAGE COPYRIGHT]";
    }

    public String getSlogan() {
        return "[PAGE SLOGAN]";
    }
}
