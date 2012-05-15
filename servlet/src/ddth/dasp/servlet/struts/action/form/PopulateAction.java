package ddth.wfp.servlet.struts.action.form;

import java.util.HashMap;
import java.util.Map;

import ddth.wfp.servlet.runtime.form.IFormLoader;
import ddth.wfp.servlet.runtime.form.WfpForm;
import ddth.wfp.utils.WfpConstants;

public class PopulateAction extends BaseFormAction {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getJsonModelInternal() {
        Map<String, String> filter = new HashMap<String, String>();
        filter.put(WfpConstants.BUNDLE_FILTER_FORM, getFormName());
        IFormLoader formLoader = getService(IFormLoader.class, filter);
        WfpForm form = formLoader != null ? formLoader.loadForm() : null;
        if (form != null) {
            return responseForm(form);
        }
        return null;
    }
}
