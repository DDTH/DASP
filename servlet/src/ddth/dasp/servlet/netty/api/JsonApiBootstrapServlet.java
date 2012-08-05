package ddth.dasp.servlet.netty.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonApiBootstrapServlet extends GenericServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JsonApiBootstrapServlet.class);

    private int port = 8082;
    private int numWorkers = 1024;
    private ExecutorService bossExecutor, workerExecutor;
    private ServerBootstrap nettyServer;
    private Timer timer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig servletConfig = getServletConfig();
        String strPort = servletConfig.getInitParameter("port");
        if (!StringUtils.isBlank(strPort)) {
            port = Integer.parseInt(strPort);
        }
        timer = new HashedWheelTimer();
        bossExecutor = Executors.newCachedThreadPool();
        workerExecutor = Executors.newCachedThreadPool();
        nettyServer = new ServerBootstrap(new NioServerSocketChannelFactory(bossExecutor,
                workerExecutor, numWorkers));
        nettyServer.setPipelineFactory(new JsonApiPipelineFactory(timer));
        nettyServer.setOption("child.tcpNoDelay", true);
        nettyServer.setOption("child.keepAlive", false);
        LOGGER.info("Netty interface is listening on port " + port);
        nettyServer.bind(new InetSocketAddress(port));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (nettyServer != null) {
                nettyServer.releaseExternalResources();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try {
            if (bossExecutor != null) {
                bossExecutor.shutdown();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try {
            if (workerExecutor != null) {
                workerExecutor.shutdown();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try {
            if (timer != null) {
                timer.stop();
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

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException,
            IOException {
    }
}
