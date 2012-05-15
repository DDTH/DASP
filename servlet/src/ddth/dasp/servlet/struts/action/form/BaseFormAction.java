package ddth.wfp.servlet.struts.action.form;

import java.util.HashMap;
import java.util.Map;

import ddth.wfp.servlet.mls.ILanguage;
import ddth.wfp.servlet.runtime.form.WfpForm;
import ddth.wfp.servlet.struts.action.JsonAction;
import ddth.wfp.utils.WfpConstants;

public abstract class BaseFormAction extends JsonAction {

    private static final long serialVersionUID = 1L;
    public static final String RESPONSE_FIELD_STATUS = "STATUS";
    public static final String RESPONSE_FIELD_MESSAGE = "MESSAGE";
    public static final String RESPONSE_FIELD_FORM = "FORM";

    public static final int STATUS_OK = 0;

    private String formName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute() throws Exception {
        formName = getServletRequest().getParameter(WfpConstants.URL_PARAM_FORM);
        return super.execute();
    }

    /**
     * Sub-classes override this method to return the JSON model.
     * 
     * @return
     */
    protected abstract Object getJsonModelInternal();

    /**
     * This method calls {@link BaseFormAction#getJsonModelInternal()} to obtain
     * the JSON model, and also add error handling routines.
     */
    @Override
    public Object getJsonModel() {
        Object result = getJsonModelInternal();
        if (result == null) {
            ILanguage lang = getLanguage();
            return responseError(404, lang.getMessage("error.notFound.form", getFormName()));
        }
        return result;
    }

    protected Object responseForm(WfpForm form) {
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("TITLE", form.getTitle());
        formData.put("UI_CONTENT", form.getUiContent());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESPONSE_FIELD_STATUS, STATUS_OK);
        result.put(RESPONSE_FIELD_FORM, formData);
        return result;
    }

    protected Object responseError(int errorCode, String errorMessage) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESPONSE_FIELD_STATUS, errorCode);
        result.put(RESPONSE_FIELD_MESSAGE, errorMessage);
        return result;
    }

    protected String getFormName() {
        return formName;
    }
}
