package ddth.dasp.framework.dbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.common.logging.JdbcConnLogger;
import ddth.dasp.common.osgi.IRequireCleanupService;
import ddth.dasp.framework.utils.TimerUtils;

/**
 * Abstract implementation of {@link IJdbcFactory}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class AbstractJdbcFactory implements IJdbcFactory,
		IRequireCleanupService {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(AbstractJdbcFactory.class);

	/**
	 * Stores established data-sources as a map of {key:datasource}.
	 */
	private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

	/**
	 * Stores currently opened connections as a map of {connection:open
	 * connection info}.
	 */
	private Map<Connection, OpenConnectionInfo> openedConnections = new HashMap<Connection, OpenConnectionInfo>();

	/**
	 * Defines maximum lifetime (ms) for a connection.
	 */
	private long maxConnectionLifetime = DbcpInfo.DEFAULT_MAX_CONNECTION_LIFETIME;

	/**
	 * Holds the parent factory. If not <code>null</code>, method calls will be
	 * first delegated to the parent factory. If the parent factory returns
	 * <code>null</code>, falls back to factory's methods.
	 */
	private IJdbcFactory parentFactory;

	/**
	 * Gets the parent {@link IJdbcFactory}.
	 * 
	 * @return parentFactory
	 */
	public IJdbcFactory getParentFactory() {
		return parentFactory;
	}

	/**
	 * Sets the parent {@link IJdbcFactory}.
	 * 
	 * @param parentFactory
	 */
	public AbstractJdbcFactory setParentFactory(IJdbcFactory parentFactory) {
		this.parentFactory = parentFactory;
		return this;
	}

	/**
	 * Getter for {@link #maxConnectionLifetime}.
	 * 
	 * @return long
	 */
	public long getMaxConnectionLifetime() {
		return maxConnectionLifetime;
	}

	/**
	 * Setter for {@link #maxConnectionLifetime}.
	 * 
	 * @param maximumConnectionLifetime
	 */
	public AbstractJdbcFactory setMaxConnectionLifetime(
			long maximumConnectionLifetime) {
		this.maxConnectionLifetime = maximumConnectionLifetime > 0 ? maximumConnectionLifetime
				: DbcpInfo.DEFAULT_MAX_CONNECTION_LIFETIME;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Builds a data-source with default connection pool settings.
	 * 
	 * @param driver
	 * @param connUrl
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	protected abstract DataSource buildDataSource(String driver,
			String connUrl, String username, String password) throws Exception;

	/**
	 * Builds a data-source with specified connection pool settings.
	 * 
	 * @param driver
	 * @param connUrl
	 * @param username
	 * @param password
	 * @param dbcpInfo
	 * @return
	 * @throws Exception
	 */
	protected abstract DataSource buildDataSource(String driver,
			String connUrl, String username, String password, DbcpInfo dbcpInfo)
			throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection(String driver, String connUrl,
			String username, String password) throws SQLException {
		return getConnection(driver, connUrl, username, password,
				getMaxConnectionLifetime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection(String driver, String connUrl,
			String username, String password, DbcpInfo dbcpInfo)
			throws SQLException {
		return getConnection(driver, connUrl, username, password,
				getMaxConnectionLifetime(), dbcpInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection(String driver, String connUrl,
			String username, String password, long maxLifetime)
			throws SQLException {
		return getConnection(driver, connUrl, username, password, maxLifetime,
				null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection(String driver, String connUrl,
			String username, String password, long maxLifetime,
			DbcpInfo dbcpInfo) throws SQLException {
		if (driver == null || connUrl == null) {
			LOGGER.error("Can not get the connection: driver and/or connection url is null!");
			return null;
		}

		/**
		 * Firstly, gets the connection from parent factory.
		 */
		Connection conn = parentFactory != null ? parentFactory.getConnection(
				driver, connUrl, username, password, maxLifetime, dbcpInfo)
				: null;
		if (conn != null) {
			return conn;
		}

		String dsName = calcHash(driver, connUrl, username, password);
		DataSource ds = getDataSource(dsName);
		if (ds == null) {
			synchronized (dataSources) {
				try {
					ds = buildDataSource(driver, connUrl, username, password,
							dbcpInfo);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				dataSources.put(dsName, ds);
			}
		}
		return getConnectionFromDataSource(dsName, ds, maxLifetime);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws SQLException
	 */
	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		return getConnection(dataSourceName, getMaxConnectionLifetime());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws SQLException
	 */
	@Override
	public Connection getConnection(String dataSourceName, long maxLifetime)
			throws SQLException {
		if (StringUtils.isBlank(dataSourceName)) {
			LOGGER.error("Can not get the connection: datasource name is empty!");
			return null;
		}

		/**
		 * Firstly, gets the connection from parent factory.
		 */
		Connection conn = parentFactory != null ? parentFactory.getConnection(
				dataSourceName, maxLifetime) : null;
		if (conn != null) {
			return conn;
		}

		DataSource ds = dataSources.get(dataSourceName);
		return getConnectionFromDataSource(dataSourceName, ds, maxLifetime);
	}

	/**
	 * Gets a connection from a specified data-source.
	 * 
	 * @param dataSourceName
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	protected Connection getConnectionFromDataSource(String dataSourceName,
			DataSource dataSource) {
		return getConnectionFromDataSource(dataSourceName, dataSource,
				getMaxConnectionLifetime());
	}

	/**
	 * Gets a datasource's information.
	 * 
	 * @param dataSource
	 *            DataSource
	 * @return int[] [numActiveConns, numIdleConns, maxConns]
	 */
	protected abstract int[] getDataSourceInfo(DataSource dataSource);

	/**
	 * Gets a connection from a specified datasource.
	 * 
	 * @param dataSourceName
	 *            String
	 * @param dataSource
	 *            DataSource
	 * @param maxConnLifetime
	 *            long maximum connection lifetime in ms
	 * @return Connection
	 * @throws SQLException
	 */
	protected Connection getConnectionFromDataSource(String dataSourceName,
			DataSource dataSource, long maxConnLifetime) {
		if (dataSource == null) {
			return null;
		}
		try {
			Connection conn = dataSource.getConnection();
			if (conn != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Obtained a connection from datasource ["
							+ dataSourceName + "]...");
				}
				JdbcConnLogger.add(conn);

				OpenConnectionInfo connInfo = new OpenConnectionInfo(
						dataSourceName);
				synchronized (openedConnections) {
					openedConnections.put(conn, connInfo);
				}
				if (maxConnLifetime > 0) {
					TimerTask task = new OpenConnectionGuardTimerTask(conn,
							connInfo);
					TimerUtils.getTimer().schedule(task, maxConnLifetime);
				}
			}
			return conn;
		} catch (SQLException e) {
			// gracefully handle the exception
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean releaseConnection(Connection conn) {
		try {
			// Try to release the connection from the parent factory first.
			if (parentFactory != null && parentFactory.releaseConnection(conn)) {
				return true;
			}
		} catch (SQLException e) {
			// this should never happen!
			LOGGER.warn("Can not release connection from parent factory!");
		}

		JdbcConnLogger.remove(conn);

		synchronized (openedConnections) {
			OpenConnectionInfo info = openedConnections.get(conn);
			if (info != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Releasing a connection from datasource ["
							+ info.getDatasourceKey() + "]..., it has lived ["
							+ info.getLifetime() + "] ms");
				}
				openedConnections.remove(conn);
				try {
					conn.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				return true;
			} else {
				LOGGER.warn("Can not find the connection in open connection list!");
				return false;
			}
		}
	}

	/**
	 * Calculates a hash string for a JDBC connection.
	 */
	protected String calcHash(String driver, String connUrl, String username,
			String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(driver != null ? driver : "NULL");
		sb.append(".");
		sb.append(connUrl != null ? connUrl : "NULL");
		sb.append(".");
		sb.append(username != null ? username : "NULL");
		sb.append(".");
		// sb.append(password != null ? password : "NULL");
		// sb.append(".");
		// return String.valueOf(sb.toString().hashCode());
		int passwordHashcode = password != null ? password.hashCode() : "NULL"
				.hashCode();
		return sb.append(passwordHashcode).toString();
	}

	/**
	 * Gets current number of opened connections.
	 * 
	 * @return int
	 */
	public int getNumOpenedConnections() {
		synchronized (openedConnections) {
			return openedConnections.size();
		}
	}

	/**
	 * Gets current number of active datasources.
	 * 
	 * @return int
	 */
	public int getNumDataSources() {
		synchronized (dataSources) {
			return dataSources.size();
		}
	}

	/**
	 * Gets a {@link OpenConnectionInfo} associated with a connection.
	 * 
	 * @param conn
	 *            Connection
	 * @return OpenConnectionInfo
	 */
	public OpenConnectionInfo getConnectionInfo(Connection conn) {
		return openedConnections.get(conn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource(String dsName) {
		synchronized (dataSources) {
			return dataSources.get(dsName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource(String driver, String connUrl,
			String username, String password) {
		String dsName = calcHash(driver, connUrl, username, password);
		return getDataSource(dsName);
		// synchronized (dataSources) {
		// DataSource ds = getDataSource(dsName);
		// if (ds == null) {
		// try {
		// ds = buildDataSource(driver, connUrl, username, password);
		// } catch (Exception e) {
		// LOGGER.error(e.getMessage(), e);
		// return null;
		// }
		// dataSources.put(dsName, ds);
		// }
		// return ds;
		// }
	}

	/**
	 * This timer task forcibly closes the opened connection if it has been
	 * occupied for so long.
	 * 
	 * @author ThanhNB
	 */
	class OpenConnectionGuardTimerTask extends TimerTask {

		private Connection conn;
		private OpenConnectionInfo openConnInfo;

		public OpenConnectionGuardTimerTask(Connection conn,
				OpenConnectionInfo openConnInfo) {
			this.conn = conn;
			this.openConnInfo = openConnInfo;
		}

		@Override
		public void run() {
			OpenConnectionInfo currentConnInfo = openedConnections.get(conn);
			if (currentConnInfo != null) {
				// found the opened connection
				if (currentConnInfo.getId() == openConnInfo.getId()) {
					// the connection instance we are holding has been occupied
					// until now!
					releaseConnection(conn);
				}
			}
		}
	}
}
