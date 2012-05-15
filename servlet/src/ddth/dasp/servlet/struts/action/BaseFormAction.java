package ddth.wfp.servlet.struts.action;

import com.opensymphony.xwork2.Action;

public class BaseFormAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private BaseForm form;
    private boolean requireCaptcha;

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute() {
        if (form == null || hasErrors()) {
            return Action.INPUT;
        } else {
            return processFormSubmission();
        }
    }

    /**
     * Performs form submission action.
     * 
     * @return
     */
    protected String processFormSubmission() {
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if (form != null) {
            validateInternal();
        }
    }

    /**
     * Sub-class overrides this method to perform its own validation.
     */
    protected void validateInternal() {
        // EMPTY
    }

    public boolean isRequireCaptcha() {
        return requireCaptcha;
    }

    public void setRequireCaptcha(boolean requireCaptcha) {
        this.requireCaptcha = requireCaptcha;
    }

    protected BaseForm getFormInternal() {
        return form;
    }

    protected void setFormInternal(BaseForm form) {
        this.form = form;
    }

    public String getUrlCaptcha() {
        if (isRequireCaptcha()) {
            return null;
        }
        return null;
    }
}
