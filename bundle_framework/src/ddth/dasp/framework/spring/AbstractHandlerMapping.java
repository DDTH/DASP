package ddth.dasp.framework.spring;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;

public abstract class AbstractHandlerMapping extends
        org.springframework.web.servlet.handler.AbstractHandlerMapping {

    private Logger LOGGER = LoggerFactory.getLogger(AbstractHandlerMapping.class);

    /**
     * Extracts the {@link IRequestParser} from the http request.
     * 
     * @param request
     * @return
     */
    protected IRequestParser getRequestParser(HttpServletRequest request) {
        Object temp = request.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
        if (!(temp instanceof IRequestParser)) {
            return null;
        }
        return (IRequestParser) temp;
    }

    /**
     * Extracts the action name from the http request.
     * 
     * @param request
     * @return
     */
    protected String getActionName(HttpServletRequest request) {
        IRequestParser rp = getRequestParser(request);
        if (rp == null) {
            LOGGER.warn("No instance of [" + IRequestParser.class + "] found!");
            return null;
        }
        return rp.getRequestAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String actionName = getActionName(request);
        return getHandlerInternal(request, actionName);
    }

    /**
     * Gets a handler to handle the specified action.
     * 
     * @param request
     * @param actionName
     * @return
     * @throws Exception
     */
    protected abstract Object getHandlerInternal(HttpServletRequest request, String actionName)
            throws Exception;
}
