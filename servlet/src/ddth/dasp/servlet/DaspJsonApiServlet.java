package ddth.dasp.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.api.IApiGroupHandler;
import ddth.dasp.common.api.IApiHandler;
import ddth.dasp.common.osgi.IOsgiBootstrap;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.common.utils.JsonUtils;

public class DaspJsonApiServlet extends HttpServlet implements CometProcessor {
    private static final long serialVersionUID = 1L;
    private final static String URI_PREFIX = "/api";

    private Logger LOGGER = LoggerFactory.getLogger(DaspJsonApiServlet.class);

    private String contextPath;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        contextPath = getServletContext().getContextPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void event(CometEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        switch (event.getEventType()) {
        case ERROR:
        case END: {
            event.close();
        }
        case BEGIN: {
            event.setTimeout(5000);// 5 seconds
            doHandleRequest(request, response);
            event.close();
        }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doHandleRequest(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doHandleRequest(request, response);
    }

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
        if (result == null) {
            result = new HashMap<String, Object>();
        }

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

    private static AtomicLong counter = new AtomicLong();

    protected void doHandleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        counter.incrementAndGet();
        try {
            String uri = request.getRequestURI();
            if (uri.startsWith(contextPath)) {
                uri = uri.substring(contextPath.length());
            }
            if (!uri.startsWith(URI_PREFIX)) {
                Map<Object, Object> res = new HashMap<Object, Object>();
                res.put(IApiHandler.RESULT_FIELD_STATUS, 500);
                res.put(IApiHandler.RESULT_FIELD_MESSAGE, "Request must starts with '/api'!");
                response.getWriter().print(JsonUtils.toJson(res));
                return;
            }
            uri = uri.substring(URI_PREFIX.length());
            String[] tokens = uri.replaceAll("^\\/+", "").replaceAll("^\\/+", "").split("\\/");
            String moduleName = tokens.length > 0 ? tokens[0] : null;
            String functionName = tokens.length > 1 ? tokens[1] : null;
            String authKey = tokens.length > 2 ? tokens[2] : null;
            Object apiParams = parseInput(request);

            IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
            Object result;
            try {
                Map<String, String> filter = new HashMap<String, String>();
                filter.put(IApiHandler.PROP_MODULE, moduleName);
                filter.put(IApiHandler.PROP_API, functionName);
                IApiHandler apiHandler = osgiBootstrap.getService(IApiHandler.class, filter);
                if (apiHandler != null) {
                    result = apiHandler.callApi(apiParams, authKey);
                } else {
                    filter.remove(IApiHandler.PROP_API);
                    IApiGroupHandler apiGroupHandler = osgiBootstrap.getService(
                            IApiGroupHandler.class, filter);
                    if (apiGroupHandler != null) {
                        result = apiGroupHandler.handleApiCall(functionName, apiParams, authKey);
                    } else {
                        Map<Object, Object> res = new HashMap<Object, Object>();
                        res.put(IApiHandler.RESULT_FIELD_STATUS, 404);
                        res.put(IApiHandler.RESULT_FIELD_MESSAGE, "No handler for [" + moduleName
                                + "/" + functionName + "]!");
                        result = res;
                    }
                }
            } catch (Exception ex) {
                Map<Object, Object> res = new HashMap<Object, Object>();
                res.put(IApiHandler.RESULT_FIELD_STATUS, 500);
                res.put(IApiHandler.RESULT_FIELD_MESSAGE, ex.getMessage());
                result = res;
            }
            response.getWriter().print(JsonUtils.toJson(result));
        } finally {
            counter.decrementAndGet();
        }
    }
}
