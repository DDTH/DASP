package ddth.wfp.servlet.struts.model;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import ddth.wfp.servlet.struts.action.BaseAction;

public class BaseModel {

    private BaseAction action;

    public BaseModel(BaseAction action) {
        this.action = action;
    }

    protected BaseAction getAction() {
        return action;
    }

    protected HttpServletRequest getRequest() {
        return action.getServletRequest();
    }

    protected ServletContext getServletContext() {
        return action.getServletContext();
    }

    /**
     * Gets the current requested URL.
     * 
     * @return String
     */
    protected String getRequestUrl() {
        return action.getRequestUrl();
    }

    /**
     * Gets the current requested URI.
     * 
     * @return String
     */
    protected String getRequestUri() {
        return action.getRequestUri();
    }
}
