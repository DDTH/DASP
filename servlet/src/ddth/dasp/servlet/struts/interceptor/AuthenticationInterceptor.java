package ddth.wfp.servlet.struts.interceptor;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import ddth.wfp.servlet.struts.action.BaseAction;
import ddth.wfp.utils.WfpConstants;

public class AuthenticationInterceptor implements Interceptor {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Object obj = actionInvocation.getAction();
        if (obj instanceof BaseAction) {
            BaseAction action = (BaseAction) obj;
            if (action.isRequireAuthentication()) {
                HttpSession session = ServletActionContext.getRequest().getSession(false);
                if (session == null || session.getAttribute(WfpConstants.SESSION_USER_ID) == null) {
                    return Action.LOGIN;
                }
            }
        }
        return actionInvocation.invoke();
    }
}
