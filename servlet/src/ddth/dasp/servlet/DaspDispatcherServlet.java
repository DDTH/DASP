package ddth.dasp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.IRequestHandler;
import ddth.dasp.common.utils.ServletUtils;
import ddth.dasp.servlet.osgi.IOsgiBootstrap;

public class DaspDispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Class<IRequestHandler> HANDLER_CLASS = IRequestHandler.class;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		doHandleRequest(request, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		doHandleRequest(request, response);
	}

	protected void doHandleRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		IOsgiBootstrap osgiBootstrap = DaspGlobal.getOsgiBootstrap();
		IRequestHandler requestHandler = osgiBootstrap
				.getService(HANDLER_CLASS);
		if (requestHandler == null) {
			ServletUtils.responseHttpError(response, 404, "No handler found!");
		}
	}
}
