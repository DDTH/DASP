package ddth.dasp.servlet.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.tempdir.TempDir;
import ddth.dasp.servlet.rp.HttpRequestParser;
import ddth.dasp.servlet.rp.IRequestParser;
import ddth.dasp.servlet.rp.MalformedRequestException;
import ddth.dasp.servlet.rp.RequestParsingInteruptedException;
import ddth.dasp.utils.DaspConstants;

public class DaspRequestListener implements ServletRequestListener {

	private Logger LOGGER = LoggerFactory.getLogger(DaspRequestListener.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event
				.getServletRequest();
		destroyTempDir(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event
				.getServletRequest();
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
		Object tmp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR);
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
		request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR,
				requestTempDir);
	}
}
