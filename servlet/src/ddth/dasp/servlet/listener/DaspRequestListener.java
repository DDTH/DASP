package ddth.dasp.servlet.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.RequestLocal;
import ddth.dasp.common.logging.JdbcConnLogger;
import ddth.dasp.common.logging.JdbcLogEntry;
import ddth.dasp.common.logging.JdbcLogger;
import ddth.dasp.common.logging.ProfileLogEntry;
import ddth.dasp.common.logging.ProfileLogger;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.rp.MalformedRequestException;
import ddth.dasp.common.rp.RequestParsingInteruptedException;
import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.servlet.rp.HttpRequestParser;

public class DaspRequestListener implements ServletRequestListener {

    private Logger LOGGER = LoggerFactory.getLogger(DaspRequestListener.class);
    private long maxUploadSize = -1, maxPostSize = -1;
    private int requestParserTimeout = -1, requestParserUploadTimeout = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        destroyTempDir(request);

        logProfiling();
        logJdbc();
        cleanUpJdbcConnections();

        RequestLocal.remove();
    }

    private void cleanUpJdbcConnections() {
        try {
            JdbcConnLogger.cleanUp();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private void logProfiling() {
        try {
            ProfileLogger.pop();
            if (LOGGER.isDebugEnabled()) {
                ProfileLogEntry profileLog = ProfileLogger.get();
                Object[] profileData = profileLog.getProfiling();
                String json = JsonUtils.toJson(profileData);
                LOGGER.debug(json);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private void logJdbc() {
        if (LOGGER.isDebugEnabled()) {
            try {
                JdbcLogEntry[] entries = JdbcLogger.get();
                for (JdbcLogEntry entry : entries) {
                    long duration = entry.getDuration();
                    String sql = entry.getSql();
                    LOGGER.debug("\t[" + duration + "] " + sql);
                }

            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        ServletContext servletContext = event.getServletContext();
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();

        // init the request local
        RequestLocal requestLocal = RequestLocal.get();
        if (requestLocal == null) {
            requestLocal = new RequestLocal();
            RequestLocal.set(requestLocal);
        }
        request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_LOCAL, requestLocal);

        String reqId = requestLocal.getId();
        ProfileLogEntry logEntry = ProfileLogger.push(reqId);
        logEntry.setRequestId(reqId);
        logEntry.setClientId(request.getRemoteAddr());

        request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_ID, reqId);
        initTempDir(request);
        initRequestParser(servletContext, request);
    }

    private void initRequestParser(ServletContext servletContext, HttpServletRequest request) {
        if (requestParserTimeout <= 0) {
            String temp = servletContext.getInitParameter("requestParserTimeout");
            try {
                requestParserTimeout = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                requestParserTimeout = IRequestParser.DEFAULT_TIMEOUT;
            }
        }
        if (requestParserUploadTimeout <= 0) {
            String temp = servletContext.getInitParameter("requestParserUploadTimeout");
            try {
                requestParserUploadTimeout = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                requestParserUploadTimeout = IRequestParser.DEFAULT_UPLOAD_TIMEOUT;
            }
        }
        if (maxPostSize <= 0) {
            String temp = servletContext.getInitParameter("maxPostSize");
            try {
                maxPostSize = Long.parseLong(temp);
            } catch (NumberFormatException e) {
                maxPostSize = IRequestParser.DEFAULT_MAX_POST_SIZE;
            }
        }
        if (maxUploadSize <= 0) {
            String temp = servletContext.getInitParameter("maxUploadSize");
            try {
                maxUploadSize = Long.parseLong(temp);
            } catch (NumberFormatException e) {
                maxUploadSize = IRequestParser.DEFAULT_MAX_UPLOAD_SIZE;
            }
        }

        // IRequestParser rp = new HttpRequestParser();
        HttpRequestParser rp = new HttpRequestParser();
        rp.setHttpRequest(request);
        if (rp.isMultipart()) {
            rp.setTimeout(requestParserUploadTimeout);
            rp.setMaxContentLength(maxUploadSize);
        } else {
            rp.setTimeout(requestParserTimeout);
            rp.setMaxContentLength(maxPostSize);
        }
        try {
            rp.parseRequest();
        } catch (RequestParsingInteruptedException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (MalformedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_PARSER, rp);
    }

    private void destroyTempDir(HttpServletRequest request) {
        Object tmp = request.getAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR);
        if (tmp instanceof TempDir) {
            try {
                ((TempDir) tmp).delete();
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }

    private void initTempDir(HttpServletRequest request) {
        String randomStr = "REQ_" + RandomStringUtils.randomAlphanumeric(16);
        TempDir contextTempDir = DaspGlobal.getContextTempDir();
        TempDir requestTempDir = new TempDir(contextTempDir, randomStr);
        request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR, requestTempDir);
    }
}
