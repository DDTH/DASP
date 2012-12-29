package ddth.dasp.servlet.netty.api;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.servlet.utils.NetUtils;

public class DaspNettyJsonApiServer {

    public static ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(
            DaspNettyJsonApiServer.class.getCanonicalName());
    private final Logger LOGGER = LoggerFactory.getLogger(DaspNettyJsonApiServer.class);

    private long readTimeoutMillisecs = 10000, writeTimeoutMillisecs = 10000;
    private int numWorkers = 32;
    private String portStr = "8082";
    private Timer timer;
    private ServerBootstrap nettyServer;

    public DaspNettyJsonApiServer() {
    }

    public long getReadTimeoutMillisecs() {
        return readTimeoutMillisecs;
    }

    public DaspNettyJsonApiServer setReadTimeoutMillisecs(long readTimeoutMillisecs) {
        this.readTimeoutMillisecs = readTimeoutMillisecs;
        return this;
    }

    public long getWriteTimeoutMillisecs() {
        return writeTimeoutMillisecs;
    }

    public DaspNettyJsonApiServer setWriteTimeoutMillisecs(long writeTimeoutMillisecs) {
        this.writeTimeoutMillisecs = writeTimeoutMillisecs;
        return this;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public DaspNettyJsonApiServer setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
        return this;
    }

    public String getPort() {
        return portStr;
    }

    public DaspNettyJsonApiServer setPort(String portStr) {
        this.portStr = portStr;
        return this;
    }

    public void start() {
        Integer port = 8082;
        if (!StringUtils.isBlank(portStr)) {
            // find free port
            String[] tokens = portStr.split("[\\s,]+");
            int[] ports = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                ports[i] = Integer.parseInt(tokens[i]);
            }
            port = NetUtils.getFreePort(ports);
        }

        timer = new HashedWheelTimer(Executors.defaultThreadFactory(), 10, TimeUnit.MILLISECONDS,
                8192);
        NioServerBossPool serverBossPool = new NioServerBossPool(Executors.newCachedThreadPool(),
                1, new ThreadNameDeterminer() {
                    private AtomicInteger COUNTER = new AtomicInteger(1);

                    @Override
                    public String determineThreadName(String currentThreadName,
                            String proposedThreadName) throws Exception {
                        int counter = COUNTER.getAndIncrement();
                        return "DNJAPI server boss #" + counter;
                    }
                });
        NioWorkerPool workerPool = new NioWorkerPool(Executors.newCachedThreadPool(), numWorkers,
                new ThreadNameDeterminer() {
                    private AtomicInteger COUNTER = new AtomicInteger(1);

                    @Override
                    public String determineThreadName(String currentThreadName,
                            String proposedThreadName) throws Exception {
                        int counter = COUNTER.getAndIncrement();
                        return "DNJAPI worker #" + counter;
                    }
                });
        nettyServer = new ServerBootstrap(new NioServerSocketChannelFactory(serverBossPool,
                workerPool));
        nettyServer.setPipelineFactory(new JsonApiPipelineFactory(timer, readTimeoutMillisecs,
                writeTimeoutMillisecs));
        nettyServer.setOption("child.tcpNoDelay", true);
        nettyServer.setOption("child.keepAlive", false);
        nettyServer.bind(new InetSocketAddress(port));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("DASP Netty Json API interface is listening on port: " + port
                    + " / Read timeout: " + readTimeoutMillisecs + " / Write timeout: "
                    + writeTimeoutMillisecs + " / Num workers: " + numWorkers);
        }
    }

    public void destroy() {
        try {
            if (nettyServer != null) {
                nettyServer.releaseExternalResources();
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
        try {
            ALL_CHANNELS.close();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
