package ddth.wfp.servlet.struts.result;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;

import ddth.wfp.utils.JsonUtils;

/**
 * JSON result type.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class JsonResult implements Result {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_PARAM = "modelName";
    private static final String DEFAULT_MODEL_NAME = "jsonModel";

    private String modelName = DEFAULT_MODEL_NAME;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(ActionInvocation actionInvocation) throws Exception {
        ValueStack valueStack = actionInvocation.getStack();
        Object jsonModel = valueStack.findValue(getModelName());
        String jsonString = JsonUtils.toJson(jsonModel);

        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(jsonString);
    }
}
