package ddth.dasp.framework.dbc;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of {@link IJdbcFactory} utilizes Apache's DBCP as the
 * connection pooling back-end.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DbcpJdbcFactory extends AbstractJdbcFactory {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DbcpJdbcFactory.class);

	protected final static int DEFAULT_MAX_ACTIVE = 8;
	protected final static int DEFAULT_MAX_IDLE = 4;
	protected final static long DEFAULT_MAX_WAIT = DEFAULT_MAX_CONNECTION_LIFETIME + 1000;
	protected final static int DEFAULT_MIN_IDLE = 1;

	private int maxActive = DEFAULT_MAX_ACTIVE, maxIdle = DEFAULT_MAX_IDLE,
			minIdle = DEFAULT_MIN_IDLE;
	private long maxWait = DEFAULT_MAX_WAIT;

	/**
	 * Gets default pool's max active connections.
	 * 
	 * @return int
	 */
	public int getMaxActive() {
		return maxActive;
	}

	/**
	 * Sets default pool's max active connections.
	 * 
	 * @param maxActive
	 *            int
	 */
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	/**
	 * Gets default pool's max idle connections.
	 * 
	 * @return int
	 */
	public int getMaxIdle() {
		return maxIdle;
	}

	/**
	 * Sets default pool's max idle connections.
	 * 
	 * @param maxIdle
	 */
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int[] getDataSourceInfo(DataSource dataSource) {
		int[] info = new int[] { -1, -1, -1 };
		if (dataSource instanceof BasicDataSource) {
			info[0] = ((BasicDataSource) dataSource).getNumActive();
			info[1] = ((BasicDataSource) dataSource).getNumIdle();
			info[2] = ((BasicDataSource) dataSource).getMaxActive();
		}
		return info;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource buildDataSource(String driver, String connUrl,
			String username, String password) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Building a datasource {driver:" + driver
					+ ";connUrl:" + connUrl + ";username:" + username
					+ ";maxActive:" + maxActive + ";maxWait:" + maxWait
					+ ";minIdle:" + minIdle + ";maxIdle:" + maxIdle + "}...");
		}
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(connUrl);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setMaxActive(maxActive);
		ds.setMaxIdle(maxIdle);
		ds.setMaxWait(maxWait);
		ds.setMinIdle(minIdle);
		String validationQuery = getValidationQuery(driver);
		if (!StringUtils.isBlank(validationQuery)) {
			ds.setValidationQuery(validationQuery);
			// PostgreSQL still not support the set query timeout method
			if (driver != null && !driver.contains("postgresql")) {
				// set the validation query timeout to 2 seconds
				ds.setValidationQueryTimeout(2);
			}
		}
		return ds;
	}

	/**
	 * Get the validation query of different databases
	 * 
	 * @param driverName
	 * @return
	 */
	protected String getValidationQuery(String driverName) {
		if (driverName.contains("mysql") || driverName.contains("postgresql")
				|| driverName.contains("sqlserver")) {
			return "SELECT 1";
		}
		if (driverName.contains("oracle")) {
			return "SELECT 1 FROM DUAL";
		}
		return "";
	}
}
