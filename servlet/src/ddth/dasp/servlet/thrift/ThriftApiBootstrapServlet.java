package ddth.dasp.servlet.thrift;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.servlet.thrift.serverfactory.IServerFactory;
import ddth.dasp.servlet.thrift.serverfactory.ThreadedSelectorServerFactory;
import ddth.dasp.servlet.thrift.serverfactory.ThreadedServerFactory;
import ddth.dasp.servlet.utils.NetUtils;

public class ThriftApiBootstrapServlet extends GenericServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ThriftApiBootstrapServlet.class);

	private int port = 9090;
	private boolean nonblockingServer = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		ServletConfig servletConfig = getServletConfig();
		String strNonblockingServer = servletConfig
				.getInitParameter("nonblockingServer");
		if (!StringUtils.isBlank(strNonblockingServer)) {
			nonblockingServer = BooleanUtils.toBoolean(strNonblockingServer);
		}
		String strPort = servletConfig.getInitParameter("port");
		if (!StringUtils.isBlank(strPort)) {
			// find free port
			String[] tokens = strPort.split("[\\s,]+");
			int[] ports = new int[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				ports[i] = Integer.parseInt(tokens[i]);
			}
			Integer port = NetUtils.getFreePort(ports);
			if (port != null) {
				this.port = port;
			}
		}

		TProcessor processor = new DaspJsonApi.Processor<DaspJsonApi.Iface>(
				new DaspJsonApiHandler());
		// TProcessor processor = new DaspProcessor(new DaspJsonApiHandler());
		IServerFactory serverFactory = nonblockingServer ? new ThreadedSelectorServerFactory(
				port, processor) : new ThreadedServerFactory(port, processor);
		ThriftUtils.startThriftServer(serverFactory);
		LOGGER.info("Thrift interface is listening on port " + port);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		super.destroy();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNonblockingServer() {
		return nonblockingServer;
	}

	public void setNonblockingServer(boolean nonblockingServer) {
		this.nonblockingServer = nonblockingServer;
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
	}
}
