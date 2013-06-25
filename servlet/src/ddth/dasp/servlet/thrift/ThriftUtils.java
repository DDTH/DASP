package ddth.dasp.servlet.thrift;

import java.util.concurrent.TimeUnit;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args.AcceptPolicy;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.servlet.thrift.serverfactory.IServerFactory;

public class ThriftUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftUtils.class);

    public static TServer createThreadedServer(TProcessorFactory processorFactory, int port,
            int clientTimeoutMillisecs, int maxFrameSize) throws TTransportException {
        int maxWorkerThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
        TServerTransport transport = new TServerSocket(port, clientTimeoutMillisecs);
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).minWorkerThreads(1)
                .maxWorkerThreads(maxWorkerThreads);
        TThreadPoolServer server = new TThreadPoolServer(args);
        return server;
    }

    public static TServer createNonBlockingServer(TProcessorFactory processorFactory, int port,
            int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize)
            throws TTransportException {
        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TNonblockingServer.Args args = new TNonblockingServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory);
        args.maxReadBufferBytes = maxReadBufferSize;
        TNonblockingServer server = new TNonblockingServer(args);
        return server;
    }

    public static TServer createHaHsServer(TProcessorFactory processorFactory, int port,
            int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize)
            throws TTransportException {
        int numThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        THsHaServer.Args args = new THsHaServer.Args(transport).processorFactory(processorFactory)
                .protocolFactory(protocolFactory).transportFactory(transportFactory)
                .workerThreads(numThreads).stopTimeoutVal(60).stopTimeoutUnit(TimeUnit.SECONDS);
        args.maxReadBufferBytes = maxReadBufferSize;
        THsHaServer server = new THsHaServer(args);
        return server;
    }

    public static TServer createThreadedSelectorServer(TProcessorFactory processorFactory,
            int port, int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize)
            throws TTransportException {
        int numThreads = Math.max(2, Runtime.getRuntime().availableProcessors());
        int selectorThreads = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        TNonblockingServerTransport transport = new TNonblockingServerSocket(port,
                clientTimeoutMillisecs);
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).workerThreads(numThreads)
                .acceptPolicy(AcceptPolicy.FAIR_ACCEPT).acceptQueueSizePerThread(10000)
                .selectorThreads(selectorThreads);
        args.maxReadBufferBytes = maxReadBufferSize;
        TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        return server;
    }

    public static void startThriftServer(final IServerFactory serverFactory) {
        Thread serverThread = new Thread(new Runnable() {
            public void run() {
                TServer server = null;
                try {
                    server = serverFactory.createServer();
                    server.serve();
                } catch (Throwable e) {
                    String msg = "Thrift server crashed: " + e.getMessage();
                    LOGGER.error(msg, e);
                    server.stop();
                    startThriftServer(serverFactory);
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void startThriftServer(final TServer server) {
        Thread serverThread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.serve();
                } catch (Throwable e) {
                    String msg = "Thrift server crashed: " + e.getMessage();
                    LOGGER.error(msg, e);
                    server.stop();
                    startThriftServer(server);
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }
}
