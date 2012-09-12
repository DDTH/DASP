package ddth.dasp.servlet.thrift.serverfactory;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;

public interface IServerFactory {
	public TServer createServer() throws TException;
}
