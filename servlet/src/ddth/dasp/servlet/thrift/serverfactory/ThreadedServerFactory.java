package ddth.dasp.servlet.thrift.serverfactory;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

import ddth.dasp.servlet.thrift.ThriftUtils;

public class ThreadedServerFactory implements IServerFactory {
	private int port;
	private TProcessorFactory processorFactory;

	public ThreadedServerFactory(int port, TProcessor processor) {
		this.port = port;
		processorFactory = new TProcessorFactory(processor);
	}

	@Override
	public TServer createServer() throws TTransportException {
		TServer server = ThriftUtils.createThreadedServer(processorFactory,
				port);
		return server;
	}
}
