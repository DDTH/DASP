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

import ddth.dasp.servlet.thrift.serverfactory.IServerFactory;

public class ThriftUtils {
	private final static int CLIENT_TIMEOUT = 1000;

	public static TServer createThreadedServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		TServerTransport transport = new TServerSocket(port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory();
		TThreadPoolServer server = new TThreadPoolServer(
				new TThreadPoolServer.Args(transport)
						.processorFactory(processorFactory)
						.protocolFactory(protocolFactory)
						.transportFactory(transportFactory)
						.maxWorkerThreads(Integer.MAX_VALUE));
		return server;
	}

	public static TServer createNonBlockingServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory();
		TNonblockingServer server = new TNonblockingServer(
				new TNonblockingServer.Args(transport)
						.processorFactory(processorFactory)
						.protocolFactory(protocolFactory)
						.transportFactory(transportFactory));
		return server;
	}

	public static TServer createHaHsServer(TProcessorFactory processorFactory,
			int port) throws TTransportException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory();
		THsHaServer server = new THsHaServer(new THsHaServer.Args(transport)
				.processorFactory(processorFactory)
				.protocolFactory(protocolFactory)
				.transportFactory(transportFactory).workerThreads(numThreads));
		return server;
	}

	public static TServer createThreadedSelectorServer(
			TProcessorFactory processorFactory, int port)
			throws TTransportException {
		int numThreads = Runtime.getRuntime().availableProcessors();
		TNonblockingServerTransport transport = new TNonblockingServerSocket(
				port, CLIENT_TIMEOUT);
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
		TTransportFactory transportFactory = new TFramedTransport.Factory();
		TThreadedSelectorServer server = new TThreadedSelectorServer(
				new TThreadedSelectorServer.Args(transport)
						.processorFactory(processorFactory)
						.protocolFactory(protocolFactory)
						.transportFactory(transportFactory)
						.workerThreads(numThreads)
						.acceptPolicy(AcceptPolicy.FAIR_ACCEPT)
						.acceptQueueSizePerThread(10000)
						.selectorThreads(numThreads).workerThreads(numThreads));
		return server;
	}

	public static void startThriftServer(final IServerFactory serverFactory) {
		Thread serverThread = new Thread(new Runnable() {
			public void run() {
				TServer server = null;
				try {
					server = serverFactory.createServer();
					server.serve();
				} catch (Exception e) {
					e.printStackTrace();
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
				} catch (Exception e) {
					e.printStackTrace();
					server.stop();
					startThriftServer(server);
				}
			}
		});
		serverThread.setDaemon(true);
		serverThread.start();
	}
}
