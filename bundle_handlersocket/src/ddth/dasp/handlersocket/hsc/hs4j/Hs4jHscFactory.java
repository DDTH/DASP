package ddth.dasp.handlersocket.hsc.hs4j;

import ddth.dasp.handlersocket.hsc.AbstractHscFactory;
import ddth.dasp.handlersocket.hsc.IHsc;
import ddth.dasp.handlersocket.hsc.IHscFactory;

/**
 * HS4J implementation of {@link IHscFactory}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class Hs4jHscFactory extends AbstractHscFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IHsc buildConnection(String server, int port, boolean readWrite) {
		IHsc conn = new Hs4jHsc(server, port, readWrite);
		return conn;
	}
}
