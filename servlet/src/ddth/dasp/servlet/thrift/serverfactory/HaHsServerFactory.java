package ddth.dasp.servlet.thrift.serverfactory;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

import ddth.dasp.servlet.thrift.ThriftUtils;

public class HaHsServerFactory implements IServerFactory {
	private int port;
	private TProcessorFactory processorFactory;

	public HaHsServerFactory(int port, TProcessor processor) {
		this.port = port;
		processorFactory = new TProcessorFactory(processor);
	}

	@Override
	public TServer createServer() throws TTransportException {
		TServer server = ThriftUtils.createHaHsServer(processorFactory, port);
		return server;
	}
}
