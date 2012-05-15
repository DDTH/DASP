package ddth.dasp.servlet.listener;

import java.io.File;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Object tmp = request
				.getAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR);
		if (tmp instanceof File) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting request temp dir [" + tmp + "]...");
			}
			FileUtils.deleteQuietly((File) tmp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event
				.getServletRequest();
		File requestTempDir = createRequestTempDir();
		request.setAttribute(DaspConstants.REQ_ATTR_CONTEXT_TEMP_DIR,
				ContextTempDirListener.getTempDir());
		request.setAttribute(DaspConstants.REQ_ATTR_REQUEST_TEMP_DIR,
				requestTempDir);

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

	protected File createRequestTempDir() {
		File contextTempDir = ContextTempDirListener.getTempDir();
		String randomStr = "REQ_" + RandomStringUtils.randomAlphanumeric(16);
		File requestTempDir = new File(contextTempDir, randomStr);
		requestTempDir.mkdirs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created request temp dir [" + requestTempDir + "].");
		}
		return requestTempDir;
	}
}
