package ddth.dasp.framework.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;

/**
 * This {@link HandlerMapping} obtains a Map&lt;String, Object&gt; and
 * simply looks up the handler from the map.
 * 
 * A handler named '*' is will catch all mappings if the specific handler is not
 * mapped.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class SimpleHandlerMapping extends AbstractHandlerMapping {

    private Logger LOGGER = LoggerFactory.getLogger(SimpleHandlerMapping.class);
    private Map<String, ?> handlerMapping;

    protected Map<String, ?> getHandlerMapping() {
        return handlerMapping;
    }

    public void setHandlerMapping(Map<String, ?> handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getHandlerInternal(HttpServletRequest request, String actionName)
            throws Exception {
        Map<String, ?> handlerMapping = getHandlerMapping();
        if (handlerMapping == null) {
            return null;
        }
        Object controller = handlerMapping.get(actionName);
        if (controller != null) {
            if (LOGGER.isDebugEnabled()) {
                String msg = "Found handler for action [" + actionName + "].";
                LOGGER.debug(msg);
            }
            return controller;
        }
        controller = handlerMapping.get("*");
        if (controller != null) {
            if (LOGGER.isDebugEnabled()) {
                String msg = "Found universal handler for action [" + actionName + "].";
                LOGGER.debug(msg);
            }
            return controller;
        }
        if (LOGGER.isDebugEnabled()) {
            String msg = "Found no handler for action [" + actionName + "].";
            LOGGER.debug(msg);
        }
        return null;
    }
}
