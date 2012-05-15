package ddth.wfp.servlet.struts.action;

/**
 * Base action class for JSON responses.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class JsonAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    /**
     * Default method that returns JSON-based model.
     * 
     * @return
     */
    public abstract Object getJsonModel();
}
