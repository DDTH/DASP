package ddth.wfp.servlet.struts.model;

import ddth.wfp.servlet.struts.action.BaseAction;

/**
 * Models the Application information
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class ApplicationModel extends BaseModel {

    private final static String APP_NAME = "WFP";
    private final static String APP_VERSION = "0.1";

    public ApplicationModel(BaseAction action) {
        super(action);
    }

    /**
     * Model: application name
     * 
     * @return
     */
    public String getName() {
        return APP_NAME;
    }

    public String getVersion() {
        return APP_VERSION;
    }
}
