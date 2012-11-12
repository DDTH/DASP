package ddth.dasp.framework.dbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * JDBC factory to create/release JDBC connections.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IJdbcFactory {

	public final static String DBDRIVER_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public final static String DBDRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public final static String DBDRIVER_PGSQL = "org.postgresql.Driver";
	public final static String DBDRIVER_HSQLDB = "org.hsqldb.jdbc.JDBCDriver";

	/**
	 * Initializing method.
	 */
	public void init();

	/**
	 * Destruction method.
	 */
	public void destroy();

	/**
	 * Gets an existing data-source by name.
	 * 
	 * @param dsName
	 * 
	 * @return an existing data source, or <code>null</code> if not exists
	 */
	public DataSource getDataSource(String dsName);

	/**
	 * Gets an existing data source that matches a connection information.
	 * 
	 * @param driver
	 *            name of the JDBC drver
	 * @param connUrl
	 *            connection string
	 * @param username
	 *            user to connect to database
	 * @param password
	 *            password to connect to database
	 * @return an existing data source, or <code>null</code> if not exists
	 */
	public DataSource getDataSource(String driver, String connUrl,
			String username, String password);

	/**
	 * Establishes a JDBC connection with default maximum lifetime and default
	 * connection pool settings.
	 * 
	 * @param driver
	 *            JDBC driver to use
	 * @param connUrl
	 *            connection string
	 * @param username
	 *            user to connect to database
	 * @param password
	 *            password to connect to database
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String driver, String connUrl,
			String username, String password) throws SQLException;

	/**
	 * Establishes a JDBC connection with default maximum lifetime and specified
	 * connection pool settings.
	 * 
	 * @param driver
	 *            JDBC driver to use
	 * @param connUrl
	 *            connection string
	 * @param username
	 *            user to connect to database
	 * @param password
	 *            password to connection to database
	 * @param dbcpInfo
	 *            connection pool settings, in the case a new pool is required
	 * @return
	 */
	public Connection getConnection(String driver, String connUrl,
			String username, String password, DbcpInfo dbcpInfo)
			throws SQLException;

	/**
	 * Establishes a JDBC connection with specified maximum lifetime.
	 * 
	 * @param driver
	 *            JDBC driver to use
	 * @param connUrl
	 *            connection string
	 * @param username
	 *            user name to connect to database
	 * @param password
	 *            password to connect to database
	 * @param maxLifetime
	 *            connection's maximum lifetime (in ms)
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String driver, String connUrl,
			String username, String password, long maxLifetime)
			throws SQLException;

	/**
	 * Establishes a JDBC connection with specified maximum life time and
	 * connection pool settings.
	 * 
	 * @param driver
	 *            JDBC driver to use
	 * @param connUrl
	 *            connection string
	 * @param username
	 *            user name to connect to database
	 * @param password
	 *            password to connect to database
	 * @param maxLifetime
	 *            connection's maximum lifetime (in ms)
	 * @param dbcpInfo
	 *            connection pool settings, in the case a new pool is required
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String driver, String connUrl,
			String username, String password, long maxLifetime,
			DbcpInfo dbcpInfo) throws SQLException;

	/**
	 * Gets a JDBC connection from a specified data-source with default maximum
	 * lifetime.
	 * 
	 * @param dataSourceName
	 *            data-source name
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException;

	/**
	 * Gets a JDBC connection from a specified data-source with specified
	 * maximum lifetime.
	 * 
	 * @param dataSourceName
	 *            data-source name
	 * @param maxLifetime
	 *            connection's maximum lifetime (in ms)
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName, long maxLifetime)
			throws SQLException;

	/**
	 * Releases an established JDBC connection.
	 * 
	 * @param conn
	 *            Connection
	 * @return boolean
	 */
	public boolean releaseConnection(Connection conn) throws SQLException;
}
