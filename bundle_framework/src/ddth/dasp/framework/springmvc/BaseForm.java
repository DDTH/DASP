package ddth.dasp.framework.springmvc;

import java.util.HashMap;
import java.util.Map;

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
