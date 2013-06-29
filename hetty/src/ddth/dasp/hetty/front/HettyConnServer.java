package ddth.dasp.hetty.front;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.config.IConfigDao;
import ddth.dasp.hetty.HettyConstants;
import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.qnt.IQueueWriter;
import ddth.dasp.hetty.utils.HettyControlPanelHttp;
import ddth.dasp.hetty.utils.HettyUtils;
import ddth.dasp.servlet.utils.NetUtils;

public class HettyConnServer {
    private final Logger LOGGER = LoggerFactory.getLogger(HettyConnServer.class);

    private IQueueWriter queueWriter;
    private IMessageFactory messageFactory;
    private long readTimeoutMillisecs = 10000, writeTimeoutMillisecs = 10000;
    private int numWorkers = 32;
    private String portStr = "8083";

    private Timer timer;
    private ServerBootstrap nettyServer;

    private static HettyPipelineFactory hettyPipelineFactory;

    private static Map<String, Object> hostQueueNameMapping = new ConcurrentHashMap<String, Object>();

    public static Map<String, Object> getHostQueueNameMapping() {
        return hostQueueNameMapping;
    }

    public static void setHostQueueNameMapping(Map<String, Object> hostQueueNameMapping) {
        HettyConnServer.hostQueueNameMapping = hostQueueNameMapping;
    }

    public static void addHostQueueNameMapping(String host, Object mapping) {
        hostQueueNameMapping.put(host, mapping);
    }

    public static void deleteHostQueueNameMapping(String host) {
        hostQueueNameMapping.remove(host);
    }

    public HettyConnServer() {
    }

    public IQueueWriter getQueueWriter() {
        return queueWriter;
    }

    public HettyConnServer setQueueWriter(IQueueWriter queueWriter) {
        this.queueWriter = queueWriter;
        return this;
    }

    public IMessageFactory getMessageFactory() {
        return messageFactory;
    }

    public HettyConnServer setMessageFactory(IMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
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

    public String getPort() {
        return portStr;
    }

    public HettyConnServer setPort(String portStr) {
        this.portStr = portStr;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        IConfigDao configDao = DaspGlobal.getOsgiBootstrap().getService(IConfigDao.class);
        hostQueueNameMapping = (Map<String, Object>) configDao.getConfig(
                HettyControlPanelHttp.MODULE, HettyControlPanelHttp.CONFIG_KEY);
        if (hostQueueNameMapping == null || hostQueueNameMapping.size() == 0) {
            hostQueueNameMapping = new ConcurrentHashMap<String, Object>();
            hostQueueNameMapping.put("127.0.0.1", HettyConstants.DEFAULT_HETTY_QUEUE);
            hostQueueNameMapping.put("localhost", HettyConstants.DEFAULT_HETTY_QUEUE);
            configDao.setConfig(HettyControlPanelHttp.MODULE, HettyControlPanelHttp.CONFIG_KEY,
                    hostQueueNameMapping);
        }

        Integer port = 8083;
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
                        return "Hetty server boss #" + counter;
                    }
                });
        NioWorkerPool workerPool = new NioWorkerPool(Executors.newCachedThreadPool(), numWorkers,
                new ThreadNameDeterminer() {
                    private AtomicInteger COUNTER = new AtomicInteger(1);

                    @Override
                    public String determineThreadName(String currentThreadName,
                            String proposedThreadName) throws Exception {
                        int counter = COUNTER.getAndIncrement();
                        return "Hetty worker #" + counter;
                    }
                });
        nettyServer = new ServerBootstrap(new NioServerSocketChannelFactory(serverBossPool,
                workerPool));
        if (hettyPipelineFactory == null) {
            hettyPipelineFactory = new HettyPipelineFactory(queueWriter, messageFactory, timer,
                    readTimeoutMillisecs, writeTimeoutMillisecs);
        }
        nettyServer.setPipelineFactory(hettyPipelineFactory);
        nettyServer.setOption("child.tcpNoDelay", true);
        nettyServer.setOption("child.keepAlive", false);
        nettyServer.bind(new InetSocketAddress(port));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Hetty interface is listening on port: " + port + " / Read timeout: "
                    + readTimeoutMillisecs + " / Write timeout: " + writeTimeoutMillisecs
                    + " / Num workers: " + numWorkers);
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
            HettyUtils.ALL_CHANNELS.close();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
