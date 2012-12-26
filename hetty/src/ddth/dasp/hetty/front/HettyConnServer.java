package ddth.dasp.hetty.front;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.hetty.qnt.IQueueWriter;

public class HettyConnServer {
    public static ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(
            HettyConnServer.class.getCanonicalName());
    private final Logger LOGGER = LoggerFactory.getLogger(HettyConnServer.class);

    private IQueueWriter queueWriter;
    private long readTimeoutMillisecs = 10000, writeTimeoutMillisecs = 10000;
    private int numWorkers = 32, port = 8083;

    private Timer timer;
    private ExecutorService bossExecutor, workerExecutor;
    private ServerBootstrap nettyServer;

    public HettyConnServer() {
    }

    public IQueueWriter getQueueWriter() {
        return queueWriter;
    }

    public HettyConnServer setQueueWriter(IQueueWriter queueWriter) {
        this.queueWriter = queueWriter;
        return this;
    }

    public long getReadTimeoutMillisecs() {
        return readTimeoutMillisecs;
    }

    public HettyConnServer setReadTimeoutMillisecs(long readTimeoutMillisecs) {
        this.readTimeoutMillisecs = readTimeoutMillisecs;
        return this;
    }

    public long getWriteTimeoutMillisecs() {
        return writeTimeoutMillisecs;
    }

    public HettyConnServer setWriteTimeoutMillisecs(long writeTimeoutMillisecs) {
        this.writeTimeoutMillisecs = writeTimeoutMillisecs;
        return this;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public HettyConnServer setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
        return this;
    }

    public int getPort() {
        return port;
    }

    public HettyConnServer setPort(int port) {
        this.port = port;
        return this;
    }

    public void start() {
        timer = new HashedWheelTimer(Executors.defaultThreadFactory(), 10, TimeUnit.MILLISECONDS,
                8192);
        bossExecutor = Executors.newCachedThreadPool();
        workerExecutor = Executors.newCachedThreadPool();
        nettyServer = new ServerBootstrap(new NioServerSocketChannelFactory(bossExecutor,
                workerExecutor, numWorkers));
        nettyServer.setPipelineFactory(new HettyPipelineFactory(queueWriter, timer,
                readTimeoutMillisecs, writeTimeoutMillisecs));
        nettyServer.setOption("child.tcpNoDelay", true);
        nettyServer.setOption("child.keepAlive", false);
        nettyServer.bind(new InetSocketAddress(port));
        LOGGER.info("Hetty interface is listening on port " + port);
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
        try {
            ALL_CHANNELS.close();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
