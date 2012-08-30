package ddth.dasp.handlersocket.hsc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IHsc {
	public void init();

	public void destroy();

	public String getName();

	public boolean isReadWrite();

	/**
	 * Inserts a row to table.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public boolean insert(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] values)
			throws SQLException;

	/**
	 * Updates rows in table and returns number of affected rows.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param values
	 * @param findValues
	 * @return
	 * @throws SQLException
	 */
	public int update(String dbName, String tableName, String tableIndexName,
			String[] columns, Object[] values, Object[] findValues)
			throws SQLException;

	/**
	 * Deletes rows from table and returns number of affefted rows.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param findValues
	 * @return
	 * @throws SQLException
	 */
	public int delete(String dbName, String tableName, String tableIndexName,
			String[] columns, Object[] findValues) throws SQLException;

	/**
	 * Finds rows and returns the result set.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param findValues
	 * @return
	 * @throws SQLException
	 */
	public ResultSet select(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] findValues)
			throws SQLException;

	/**
	 * Finds rows and returns the result set.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param findValues
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	public ResultSet select(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] findValues,
			int limit) throws SQLException;

	/**
	 * Finds rows and returns the result set.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param tableIndexName
	 * @param columns
	 * @param findValues
	 * @param limit
	 * @param offset
	 * @return
	 * @throws SQLException
	 */
	public ResultSet select(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] findValues,
			int limit, int offset) throws SQLException;
}
