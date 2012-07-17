package ddth.dasp.servlet.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.id.IdGenerator;
import ddth.dasp.common.logging.ProfileLogEntry;
import ddth.dasp.common.logging.ProfileLogger;
import ddth.dasp.common.rp.IRequestParser;
import ddth.dasp.common.rp.RequestParsingInteruptedException;
import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.common.utils.DaspConstants;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.servlet.rp.HttpRequestParser;
import ddth.dasp.servlet.rp.MalformedRequestException;

public class DaspRequestListener implements ServletRequestListener {

    private Logger LOGGER = LoggerFactory.getLogger(DaspRequestListener.class);
    private static final IdGenerator idGen = IdGenerator.getInstance(IdGenerator.getMacAddr());

    protected static String generateId() {
        long id = idGen.generateId64();
        StringBuffer hex = new StringBuffer(Long.toHexString(id));
        while (hex.length() < 16) {
            hex.insert(0, '0');
        }
        return hex.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        destroyTempDir(request);
        ProfileLogger.pop();
        ProfileLogEntry profileLog = ProfileLogger.get();
        Object[] profileData = profileLog.getProfiling();
        String json = JsonUtils.toJson(profileData);
        System.out.println(json);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestInitialized(ServletRequestEvent event) {
        String reqId = generateId();
        ProfileLogger.push(reqId);
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_ID, reqId);
        initTempDir(request);
        initRequestParser(request);
    }

    private void initRequestParser(HttpServletRequest request) {
        IRequestParser rp = new HttpRequestParser();
        ((HttpRequestParser) rp).setHttpRequest(request);
        rp.reset();
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
