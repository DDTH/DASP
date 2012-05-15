package ddth.wfp.servlet.runtime.form;

import java.io.Serializable;

/**
 * Represents an application's form
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class WfpForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name, title, uiContent, description;

    public WfpForm() {
    }

    public WfpForm(String name) {
        setName(name);
    }

    public WfpForm(String name, String title, String uiContent, String description) {
        setName(name);
        setTitle(title);
        setDescription(description);
        setUiContent(uiContent);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUiContent() {
        return uiContent;
    }

    public void setUiContent(String uiContent) {
        this.uiContent = uiContent;
    }
}
