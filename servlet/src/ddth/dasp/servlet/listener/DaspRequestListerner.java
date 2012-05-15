package ddth.dasp.servlet.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import ddth.dasp.servlet.rp.HttpRequestParser;
import ddth.dasp.servlet.rp.IRequestParser;
import ddth.dasp.servlet.rp.MalformedRequestException;
import ddth.dasp.servlet.rp.RequestParsingInteruptedException;

public class DaspRequestListerner implements ServletRequestListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestDestroyed(ServletRequestEvent event) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event
				.getServletRequest();
		IRequestParser rp = new HttpRequestParser();
		rp.reset();
		try {
			rp.parseRequest();
		} catch (RequestParsingInteruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
