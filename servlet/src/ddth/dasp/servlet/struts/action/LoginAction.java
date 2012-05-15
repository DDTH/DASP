package ddth.wfp.servlet.struts.action;

import com.opensymphony.xwork2.Action;

import ddth.wfp.servlet.mls.ILanguage;
import ddth.wfp.servlet.runtime.us.IAuthenticationAgent;
import ddth.wfp.servlet.runtime.us.bo.IUsManager;
import ddth.wfp.servlet.runtime.us.bo.IUser;
import ddth.wfp.servlet.utils.SessionUtils;
import ddth.wfp.utils.WfpConstants;

public class LoginAction extends BaseFormAction {
    private static final long serialVersionUID = 1L;

    private IUser user;

    public LoginForm getForm() {
        return (LoginForm) super.getFormInternal();
    }

    public void setForm(LoginForm form) {
        super.setFormInternal(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateInternal() {
        ILanguage lang = getLanguage();
        LoginForm form = getForm();
        IUsManager usManager = getService(IUsManager.class);
        user = usManager.getUser(form.getEmail());
        if (user == null) {
            addActionError(lang.getMessage("error.notFound.user", form.getEmail()));
        } else {
            IAuthenticationAgent aa = getService(IAuthenticationAgent.class);
            if (!aa.authenticate(user, form.getPassword())) {
                addActionError(lang.getMessage("error.authentication.failed"));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String processFormSubmission() {
        SessionUtils.setSessionAttribute(getHttpSession(true), WfpConstants.SESSION_USER_ID, user
                .getLoginName());
        ILanguage lang = getLanguage();
        addActionMessage(lang.getMessage("msg.successful.login"));
        return Action.SUCCESS;
    }

    private String urlTransit;

    /**
     * Gets the transition url.
     * 
     * @return
     */
    public String getUrlTransit() {
        if (getForm() == null) {
            return null;
        }
        if (urlTransit == null) {
            StringBuilder url = new StringBuilder(getServletContext().getContextPath());
            url.append("/home").append(WfpConstants.URL_SUFFIX);
            urlTransit = url.toString();
        }
        return urlTransit;
    }
}
