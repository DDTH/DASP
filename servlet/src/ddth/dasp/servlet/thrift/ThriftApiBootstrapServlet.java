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
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftApiBootstrapServlet extends GenericServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ThriftApiBootstrapServlet.class);

    private int port = 9090;
    private boolean nonblockingServer = true;

    private TServer server;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig servletConfig = getServletConfig();
        String strNonblockingServer = servletConfig.getInitParameter("nonblockingServer");
        if (!StringUtils.isBlank(strNonblockingServer)) {
            nonblockingServer = BooleanUtils.toBoolean(strNonblockingServer);
        }
        String strPort = servletConfig.getInitParameter("port");
        if (!StringUtils.isBlank(strPort)) {
            port = Integer.parseInt(strPort);
        }

        TProcessor processor = new DaspJsonApi.Processor<DaspJsonApi.Iface>(
                new DaspJsonApiHandler());
        try {
            if (nonblockingServer)
                server = nonblockingServer(processor, port);
            else
                server = simpleServer(processor, port);
        } catch (TTransportException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // run Thrift server on another thread
        Thread thriftServer = new Thread(new Runnable() {
            public void run() {
                server.serve();
            }
        });
        thriftServer.setDaemon(true);
        thriftServer.start();
        LOGGER.info("Thrift interface is listening on port " + port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (server != null && server.isServing()) {
                server.stop();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
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

    /**
     * Create a simple Thrift server using ThreadPool with framed transport and
     * binary protocol for given processor.
     * 
     * @param processor
     * @param port
     * @return
     * @throws TTransportException
     */
    private static TServer simpleServer(TProcessor processor, int port) throws TTransportException {
        TServerTransport socket = new TServerSocket(port);
        TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(socket)
                .processor(processor).protocolFactory(new TBinaryProtocol.Factory())
                .transportFactory(new TFramedTransport.Factory()));
        return server;
    }

    /**
     * Create a half-sync, half-async nonblocking Thrift server using ThreadPool
     * with framed transport and binary protocol for given processor.
     * 
     * @param processor
     * @param port
     * @return
     * @throws TTransportException
     */
    private static TServer nonblockingServer(TProcessor processor, int port)
            throws TTransportException {
        TNonblockingServerTransport socket = new TNonblockingServerSocket(port);
        TServer server = new THsHaServer(new THsHaServer.Args(socket).processor(processor)
                .protocolFactory(new TBinaryProtocol.Factory())
                .transportFactory(new TFramedTransport.Factory()));
        return server;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException,
            IOException {
    }
}
