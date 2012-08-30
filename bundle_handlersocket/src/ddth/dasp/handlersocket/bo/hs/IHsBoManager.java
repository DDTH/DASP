package ddth.dasp.handlersocket.bo.hs;

import java.sql.SQLException;

import ddth.dasp.handlersocket.hsc.IHsc;

public interface IHsBoManager {
	/**
	 * Gets a database connection.
	 * 
	 * @return
	 */
	public IHsc getConnection();

	/**
	 * Releases an opening database connection.
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public void releaseConnection(IHsc conn) throws SQLException;
}
