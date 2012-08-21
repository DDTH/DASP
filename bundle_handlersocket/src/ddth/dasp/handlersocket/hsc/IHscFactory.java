package ddth.dasp.handlersocket.hsc;

/**
 * HandlerSocket factory to create/release HandlerSocket connections.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IHscFactory {

	public final static int PORT_READ = 9998;
	public final static int PORT_READWRITE = 9999;

	/**
	 * Initializing method.
	 */
	public void init();

	/**
	 * Destruction method.
	 */
	public void destroy();

	/**
	 * Gets a read-write HandlerSocket connection.
	 * 
	 * @param server
	 * @param port
	 * @return
	 */
	public IHsc getConnection(String server, int port);

	/**
	 * Gets a HandlerSocket connection.
	 * 
	 * @param server
	 * @param port
	 * @param readWrite
	 * @return
	 */
	public IHsc getConnection(String server, int port, boolean readWrite);

	/**
	 * Releases an established HandlerSocket connection.
	 * 
	 * @param conn
	 * @return boolean
	 */
	public boolean releaseConnection(IHsc conn);
}
