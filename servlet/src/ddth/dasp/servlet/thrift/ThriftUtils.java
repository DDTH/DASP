package ddth.dasp.servlet.thrift;

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
	private final static int CLIENT_TIMEOUT = 1000;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ThriftUtils.class);
	private static final long MAX_READ_BUFFER_BYTES = 1 * 1024 * 1024; // 1MB

	public static TServer createThreadedServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		TServerTransport transport = new TServerSocket(port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory(
				(int) MAX_READ_BUFFER_BYTES);
		TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
				.processorFactory(processorFactory)
				.protocolFactory(protocolFactory)
				.transportFactory(transportFactory)
				.maxWorkerThreads(Integer.MAX_VALUE);
		TThreadPoolServer server = new TThreadPoolServer(args);
		return server;
	}

	public static TServer createNonBlockingServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory(
				(int) MAX_READ_BUFFER_BYTES);
		TNonblockingServer.Args args = new TNonblockingServer.Args(transport)
				.processorFactory(processorFactory)
				.protocolFactory(protocolFactory)
				.transportFactory(transportFactory);
		args.maxReadBufferBytes = MAX_READ_BUFFER_BYTES;
		TNonblockingServer server = new TNonblockingServer(args);
		return server;
	}

	public static TServer createHaHsServer(TProcessorFactory processorFactory,
			int port) throws TTransportException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory(
				(int) MAX_READ_BUFFER_BYTES);
		THsHaServer.Args args = new THsHaServer.Args(transport)
				.processorFactory(processorFactory)
				.protocolFactory(protocolFactory)
				.transportFactory(transportFactory).workerThreads(numThreads);
		args.maxReadBufferBytes = MAX_READ_BUFFER_BYTES;
		THsHaServer server = new THsHaServer(args);
		return server;
	}

	public static TServer createThreadedSelectorServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory(
				(int) MAX_READ_BUFFER_BYTES);
		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(
				transport).processorFactory(processorFactory)
				.protocolFactory(protocolFactory)
				.transportFactory(transportFactory).workerThreads(numThreads)
				.acceptPolicy(AcceptPolicy.FAIR_ACCEPT)
				.acceptQueueSizePerThread(10000).selectorThreads(numThreads)
				.workerThreads(numThreads);
		args.maxReadBufferBytes = MAX_READ_BUFFER_BYTES;
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
