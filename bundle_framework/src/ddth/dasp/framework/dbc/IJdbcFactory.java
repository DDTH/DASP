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
     * Gets an existing data source by name.
     * 
     * @param dsName
     *            DataSource
     * @return DataSource an existing data source, or <code>null</code> if not
     *         exists
     */
    public DataSource getDataSource(String dsName);

    /**
     * Gets an existing data source that matches a connection information.
     * 
     * @param driver
     *            String name of the JDBC seriver
     * @param connUrl
     *            String connection string
     * @param username
     *            String user to connect to database
     * @param password
     *            String password to connect to database
     * @return DataSource an existing data source, or <code>null</code> if not
     *         exists
     */
    public DataSource getDataSource(String driver, String connUrl, String username, String password);

    /**
     * Establishes a JDBC connection with default maximum lifetime.
     * 
     * @param driver
     *            String JDBC driver to use
     * @param connUrl
     *            String connection string
     * @param username
     *            String user to connect to database
     * @param password
     *            String password to connect to database
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnection(String driver, String connUrl, String username, String password)
            throws SQLException;

    /**
     * Establishes a JDBC connection with specified maximum lifetime.
     * 
     * @param driver
     *            String JDBC driver to use
     * @param connUrl
     *            connection string
     * @param username
     *            user name to connect to database
     * @param password
     *            password to connect to database
     * @param maxLifetime
     *            long connection's maximum lifetime (in ms)
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnection(String driver, String connUrl, String username,
            String password, long maxLifetime) throws SQLException;

    /**
     * Gets a JDBC connection from a specified datasource with default maximum
     * lifetime.
     * 
     * @param dataSourceName
     *            String the datasource name
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnection(String dataSourceName) throws SQLException;

    /**
     * Gets a JDBC connection from a specified datasource with specified maximum
     * lifetime.
     * 
     * @param dataSourceName
     *            String the datasource name
     * @param maxLifetime
     *            long connection's maximum lifetime (in ms)
     * @return connection's maximum lifetime (in ms)
     * @throws SQLException
     */
    public Connection getConnection(String dataSourceName, long maxLifetime) throws SQLException;

    /**
     * Releases an established JDBC connection.
     * 
     * @param conn
     *            Connection
     * @return boolean
     */
    public boolean releaseConnection(Connection conn) throws SQLException;
}
