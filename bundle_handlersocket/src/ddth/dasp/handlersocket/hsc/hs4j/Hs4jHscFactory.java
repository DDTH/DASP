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

	private boolean destroying = false;

	public void destroy() {
		destroying = true;
		super.destroy();
		destroying = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IHsc buildConnection(String connName, String server, int port,
			boolean readWrite) {
		Hs4jHsc conn = new Hs4jHsc(connName, server, port, readWrite);
		conn.init();
		return conn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean releaseConnection(IHsc conn) {
		if (destroying) {
			return super.releaseConnection(conn);
		} else {
			return true;
		}
	}
}
