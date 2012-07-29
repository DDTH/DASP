package ddth.dasp.framework.api;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.Controller;

import ddth.dasp.common.api.ApiException;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.common.utils.JsonUtils;

/**
 * This {@link IApiHandler} uses JSON as message format.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DelegateJsonRestApiHandler extends DelegateRestApiHandler implements Controller {

    private Logger LOGGER = LoggerFactory.getLogger(DelegateJsonRestApiHandler.class);

    public DelegateJsonRestApiHandler() {
    }

    public DelegateJsonRestApiHandler(IApiHandler apiHandler) {
        super(apiHandler);
    }

    /**
     * {@inheritDoc}
     * 
     * This methods first parses the request's content (POST method expected) as
     * a JSON string; then, add parameters from URL (if any).
     */
    @SuppressWarnings("unchecked")
    protected Object parseInput(HttpServletRequest request) {
        Object tempRp = request.getAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER);
        if (!(tempRp instanceof IRequestParser)) {
            LOGGER.warn("No instance of [" + IRequestParser.class + "] found!");
            return null;
        }
        IRequestParser rp = (IRequestParser) tempRp;
        String rawInput = rp.getRequestContent();
        // first: parses parameters from request's content as JSON.
        Object result = JsonUtils.fromJson(rawInput);

        // second: add parameters from URL if applicable.
        if (result instanceof Map<?, ?>) {
            Map<String, String> tempMap = (Map<String, String>) result;
            for (Entry<String, String> entry : rp.getUrlParameters().entrySet()) {
                String key = entry.getKey();
                if (!tempMap.containsKey(key)) {
                    tempMap.put(key, entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * This method sends result as a JSON string to the http response.
     */
    @Override
    protected void returnResult(HttpServletResponse response, Object result) throws IOException,
            ApiException {
        String json = null;
        try {
            json = JsonUtils.toJson(result);
        } catch (Exception e) {
            throw new ApiException(e);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(json);
        response.flushBuffer();
    }
}
