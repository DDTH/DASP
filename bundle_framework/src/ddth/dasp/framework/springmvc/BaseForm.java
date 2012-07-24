package ddth.dasp.framework.springmvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;

/**
 * Use this class as starting point for application SpringMVC form.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version since v0.1.0
 */
public abstract class BaseForm {

    private String name, action, cancelAction;
    private Map<String, Object> fields = new HashMap<String, Object>();

    public BaseForm() {
        // EMPTY
    }

    public BaseForm(String name) {
        this.name = name;
    }

    public BaseForm(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public BaseForm(String name, String action, String cancelAction) {
        this.name = name;
        this.action = action;
        this.cancelAction = cancelAction;
    }

    /**
     * Populate form fields from the http request.
     * 
     * @param request
     */
    public void populateFields(HttpServletRequest request) {
        String[] fieldList = getFieldList();
        IRequestParser rp = (IRequestParser) request
                .getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
        for (String field : fieldList) {
            Object value = rp != null ? rp.getFormField(field) : request.getParameter(field);
            setField(field, value);
        }
    }

    /**
     * Gets list of fields awared by this form.
     * 
     * @return
     */
    public abstract String[] getFieldList();

    /**
     * Sets value for a field.
     * 
     * @param name
     * @param value
     */
    public void setField(String name, Object value) {
        fields.put(name, value);
    }

    /**
     * Gets value of a field.
     * 
     * @param name
     * @return
     */
    public Object getField(String name) {
        return fields.get(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCancelAction() {
        return cancelAction;
    }

    public void setCancelAction(String cancelAction) {
        this.cancelAction = cancelAction;
    }
}
