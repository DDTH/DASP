package ddth.dasp.framework.bo;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

import ddth.dasp.framework.dbc.IJdbcFactory;
import ddth.dasp.framework.dbc.JdbcUtils;
import ddth.dasp.utils.JsonUtils;
import ddth.dasp.utils.PropsUtils;

public abstract class BaseJdbcBoManager implements IJdbcBoManager {

	private final static int NUM_PROCESSORS = Runtime.getRuntime()
			.availableProcessors();

	private Logger LOGGER = LoggerFactory.getLogger(BaseJdbcBoManager.class);
	private IJdbcFactory jdbcFactory;
	private String dbDriver, dbConnUrl, dbUsername, dbPassword;
	private Properties sqlProps = new Properties();
	private ConcurrentMap<String, SqlProps> cacheSqlProps = new MapMaker()
			.concurrencyLevel(NUM_PROCESSORS).weakKeys().weakValues().makeMap();

	protected IJdbcFactory getJdbcFactory() {
		return jdbcFactory;
	}

	public void setJdbcFactory(IJdbcFactory jdbcFactory) {
		this.jdbcFactory = jdbcFactory;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public void setDbConnUrl(String dbConnUrl) {
		this.dbConnUrl = dbConnUrl;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	/**
	 * Initializing method.
	 */
	public void init() {
		loadSqlProps();
	}

	/**
	 * Destruction method.
	 */
	public void destroy() {
		// EMPTY
	}

	/**
	 * Loads SQL properties. It's in {@link Properties} format.
	 */
	protected void loadSqlProps() {
		Object sqlProps = getSqlPropsLocation();
		if (sqlProps instanceof Properties) {
			this.sqlProps.putAll((Properties) sqlProps);
		} else if (sqlProps instanceof InputStream) {
			Properties props = PropsUtils
					.loadProperties((InputStream) sqlProps);
			if (props != null) {
				this.sqlProps.putAll(props);
			}
		} else if (sqlProps != null) {
			String location = sqlProps.toString();
			InputStream is = getClass().getResourceAsStream(location);
			Properties props = PropsUtils.loadProperties(is, location
					.endsWith(".xml"));
			if (props != null) {
				this.sqlProps.putAll(props);
			}
		} else {
			String msg = "Can not load SQL properties from [" + sqlProps + "]!";
			LOGGER.warn(msg);
		}
	}

	/**
	 * Gets the SQL properties location. The location can be either of:
	 * 
	 * <ul>
	 * <li>{@link InputStream}: properties are loaded from the input stream.</li>
	 * <li>{@link Properties}: properties are copied from this one.</li>
	 * <li>{@link String}: properties are loaded from file (located within the
	 * classpath) specified by this string.</li>
	 * </ul>
	 * 
	 * @return location of the SQL properties
	 */
	protected abstract Object getSqlPropsLocation();

	/**
	 * Gets a SQL property by name.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected SqlProps getSqlProps(String name) {
		SqlProps result = cacheSqlProps.get(name);
		if (result == null) {
			String rawProps = sqlProps.getProperty(name);
			if (!StringUtils.isBlank(rawProps)) {
				try {
					Map<String, Object> props = JsonUtils.fromJson(rawProps,
							Map.class);
					result = new SqlProps();
					result.populate(props);
					cacheSqlProps.put(name, result);
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return jdbcFactory.getConnection(dbDriver, dbConnUrl, dbUsername,
				dbPassword);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void releaseConnection(Connection conn) throws SQLException {
		jdbcFactory.releaseConnection(conn);
	}

	/**
	 * Executes a SELECT query and returns the result as an array of object.
	 * 
	 * @param <T>
	 * @param sqlKey
	 * @param params
	 * @param clazz
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	protected <T extends BaseJdbcBo> T[] executeSelect(final String sqlKey,
			Map<String, Object> params, Class<T> clazz) throws SQLException {
		SqlProps sqlProps = getSqlProps(sqlKey);
		if (sqlProps == null) {
			throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
		}
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		try {
			List<T> result = new ArrayList<T>();
			PreparedStatement stm = JdbcUtils.prepareStatement(conn, sqlProps
					.getSql(), params);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				T obj = null;
				try {
					obj = clazz.newInstance();
					obj.populate(rs);
					result.add(obj);
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			JdbcUtils.closeResources(null, stm, rs);
			return result.toArray((T[]) Array.newInstance(clazz, 0));
		} finally {
			releaseConnection(conn);
		}
	}
}
