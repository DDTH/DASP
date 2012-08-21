package ddth.dasp.handlersocket.hsc;

import java.sql.SQLException;

public interface IHsc {
	public void init();

	public void destroy();

	public boolean isReadWrite();

	public boolean insert(String dbName, String tableName,
			String tableIndexName, String[] columns, Object[] values)
			throws SQLException;

	public int update(String dbName, String tableName, String tableIndexName,
			String[] columns, Object[] values, Object[] findValues)
			throws SQLException;
}
