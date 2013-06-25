package ddth.dasp.servlet.thrift.serverfactory;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

import ddth.dasp.servlet.thrift.ThriftUtils;

public class ThreadedServerFactory extends AbstractServerFactory {

    public ThreadedServerFactory(int port, TProcessor processor, int clientTimeoutMillisecs,
            int maxFrameSize) {
        super(port, processor, clientTimeoutMillisecs, maxFrameSize);
    }

    @Override
    public TServer createServer() throws TTransportException {
        TServer server = ThriftUtils.createThreadedServer(getProcessorFactory(), getPort(),
                getClientTimeoutMillisecs(), getMaxFrameSize());
        return server;
    }
}
