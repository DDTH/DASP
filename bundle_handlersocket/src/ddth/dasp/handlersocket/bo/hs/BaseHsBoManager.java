package ddth.dasp.handlersocket.bo.hs;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.common.utils.OsgiUtils;
import ddth.dasp.common.utils.PropsUtils;
import ddth.dasp.framework.bo.CachedBoManager;
import ddth.dasp.framework.bo.jdbc.BaseJdbcBoManager;
import ddth.dasp.framework.utils.EhProperties;
import ddth.dasp.handlersocket.hsc.IHsc;
import ddth.dasp.handlersocket.hsc.IHscFactory;

public abstract class BaseHsBoManager extends CachedBoManager implements
		IHsBoManager {

	private final static int NUM_PROCESSORS = Runtime.getRuntime()
			.availableProcessors();

	@SuppressWarnings("unchecked")
	private final static Map<String, Object>[] MAP_ARRAY = (Map<String, Object>[]) Array
			.newInstance(Map.class, 0);

	private Logger LOGGER = LoggerFactory.getLogger(BaseJdbcBoManager.class);

	private IHscFactory hscFactory;
	private String dbHost;
	private int dbPort;
	private boolean dbReadOnly = false;

	private Properties queryConfig = new EhProperties();
	private ConcurrentMap<String, QueryConfig> cacheQueryConfig = new MapMaker()
			.concurrencyLevel(NUM_PROCESSORS).makeMap();
	private Object queryConfigLocation;

	protected IHscFactory getHscFactory() {
		if (hscFactory != null) {
			return hscFactory;
		}
		BundleContext bundleContext = getBundleContext();
		if (bundleContext != null) {
			OsgiUtils.getService(bundleContext, IHscFactory.class);
		}
		return null;
	}

	public void setHscFactory(IHscFactory hscFactory) {
		this.hscFactory = hscFactory;
	}

	protected String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	protected int getDbPort() {
		return dbPort;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

	protected boolean isDbReadOnly() {
		return dbReadOnly;
	}

	public void setDbReadOnly(boolean dbReadOnly) {
		this.dbReadOnly = dbReadOnly;
	}

	/**
	 * Initializing method.
	 */
	public void init() {
		loadQueryConfig();
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
	protected void loadQueryConfig() {
		this.queryConfig.clear();
		this.cacheQueryConfig.clear();

		Object queryConfig = getQueryConfigLocation();
		if (queryConfig instanceof Properties) {
			this.queryConfig.putAll((Properties) queryConfig);
		} else if (queryConfig instanceof InputStream) {
			Properties props = PropsUtils
					.loadProperties((InputStream) queryConfig);
			if (props != null) {
				this.queryConfig.putAll(props);
			}
		} else if (queryConfig != null) {
			String location = queryConfig.toString();
			InputStream is = getClass().getResourceAsStream(location);
			Properties props = PropsUtils.loadProperties(is,
					location.endsWith(".xml"));
			if (props != null) {
				this.queryConfig.putAll(props);
			}
		} else {
			String msg = "Can not load query configuration from ["
					+ queryConfig + "]!";
			LOGGER.warn(msg);
		}
	}

	/**
	 * Gets the query configuration location. The location can be either of:
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
	protected Object getQueryConfigLocation() {
		return queryConfigLocation;
	}

	/**
	 * Sets the query configuration location. The location can be either of:
	 * 
	 * <ul>
	 * <li>{@link InputStream}: properties are loaded from the input stream.</li>
	 * <li>{@link Properties}: properties are copied from this one.</li>
	 * <li>{@link String}: properties are loaded from file (located within the
	 * classpath) specified by this string.</li>
	 * </ul>
	 * 
	 * @param queryConfigLocation
	 */
	public void setQueryConfigLocation(Object queryConfigLocation) {
		this.queryConfigLocation = queryConfigLocation;
	}

	/**
	 * Gets a SQL property by name.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected QueryConfig getQueryConfig(String name) {
		QueryConfig result = cacheQueryConfig.get(name);
		if (result == null) {
			String rawProps = queryConfig.getProperty(name);
			if (!StringUtils.isBlank(rawProps)) {
				try {
					Map<String, Object> props = JsonUtils.fromJson(rawProps,
							Map.class);
					result = new QueryConfig();
					result.populate(props);
					cacheQueryConfig.put(name, result);
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
	public IHsc getConnection() {
		IHsc hsc = getHscFactory().getConnection(dbHost, dbPort, !dbReadOnly);
		return hsc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void releaseConnection(IHsc conn) {
		getHscFactory().releaseConnection(conn);
	}

	private String replace(String str, Object[] replacements) {
		for (int i = 1; i < replacements.length; i++) {
			str = str.replaceAll("\\{" + i + "\\}",
					replacements[i] != null ? replacements[i].toString() : "");
		}
		return str;
	}

	/**
	 * Obtains and builds the {@link QueryConfig}.
	 * 
	 * @param configKey
	 * @return
	 */
	protected QueryConfig buildQueryConfig(final Object configKey) {
		final String finalKey = (configKey instanceof Object[]) ? ((Object[]) configKey)[0]
				.toString() : configKey.toString();
		QueryConfig queryConfig = null;
		QueryConfig tempQueryConfig = getQueryConfig(finalKey);
		if (tempQueryConfig != null) {
			queryConfig = tempQueryConfig.clone();
		}
		if (queryConfig != null && configKey instanceof Object[]) {
			Object[] temp = (Object[]) configKey;

			String dbName = queryConfig.getDbName();
			queryConfig.setDbName(replace(dbName, temp));

			String indexName = queryConfig.getIndexName();
			queryConfig.setIndexName(replace(indexName, temp));

			String tableName = queryConfig.getTableName();
			queryConfig.setTableName(replace(tableName, temp));
		}
		return queryConfig;
	}

	/**
	 * Executes a SELECT query, without cache & paging.
	 * 
	 * @param configKey
	 * @param findValues
	 * @return
	 * @throws SQLException
	 */
	protected Map<String, Object>[] execSelect(final Object configKey,
			Object[] findValues) throws SQLException {
		return execSelect(configKey, findValues, (String) null);
	}

	/**
	 * Executes a SELECT query, without paging.
	 * 
	 * @param configKey
	 * @param findValues
	 * @param cacheKey
	 * @return
	 * @throws SQLException
	 */
	protected Map<String, Object>[] execSelect(final Object configKey,
			Object[] findValues, String cacheKey) throws SQLException {
		return execSelect(configKey, findValues, cacheKey, Integer.MAX_VALUE);
	}

	/**
	 * Executes a SELECT query, and limits number of returned rows.
	 * 
	 * @param configKey
	 * @param findValues
	 * @param cacheKey
	 * @param limitRows
	 * @return
	 * @throws SQLException
	 */
	protected Map<String, Object>[] execSelect(final Object configKey,
			Object[] findValues, String cacheKey, int limitRows)
			throws SQLException {
		return execSelect(configKey, findValues, cacheKey, Integer.MAX_VALUE, 0);
	}

	/**
	 * Executes a SELECT query.
	 * 
	 * @param configKey
	 * @param findValues
	 * @param cacheKey
	 * @param limitRows
	 * @param rowOffset
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object>[] execSelect(final Object configKey,
			Object[] findValues, String cacheKey, int limitRows, int rowOffset)
			throws SQLException {
		List<Map<String, Object>> result = null;
		if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
			// get from cache
			result = (List<Map<String, Object>>) getFromCache(cacheKey);
		}
		if (result == null) {
			// cache missed
			QueryConfig queryConfig = buildQueryConfig(configKey);
			if (queryConfig == null) {
				throw new SQLException("Can not retrieve query configuration ["
						+ configKey + "]!");
			}

			IHsc conn = getConnection();
			if (conn == null) {
				throw new SQLException("Can not make connection to database!");
			}

			ResultSet resultSet = null;
			result = new ArrayList<Map<String, Object>>();
			try {
				String dbName = queryConfig.getDbName();
				String tableName = queryConfig.getTableName();
				String tableIndexName = queryConfig.getIndexName();
				String[] queryColumns = queryConfig.getColumnNames();
				resultSet = conn.select(dbName, tableName, tableIndexName,
						queryColumns, findValues);
				ColumnConfig[] columns = queryConfig.getColumns();
				while (resultSet.next()) {
					Map<String, Object> obj = new HashMap<String, Object>();
					for (int i = 1; i <= columns.length; i++) {
						String colName = columns[i - 1].getMappedName();
						Object value = columns[i - 1].getValue(resultSet);
						obj.put(colName, value);
					}
					result.add(obj);
				}
			} finally {
				if (resultSet != null) {
					try {
						resultSet.close();
					} catch (Exception e) {
					}
				}
				releaseConnection(conn);
			}
		}
		if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
			// put to cache
			putToCache(cacheKey, result);
		}
		return result.toArray(MAP_ARRAY);
	}

	/**
	 * Execute an INSERT statement.
	 * 
	 * @param configKey
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	protected boolean execInsert(final Object configKey, Object[] values)
			throws SQLException {
		QueryConfig queryConfig = buildQueryConfig(configKey);
		if (queryConfig == null) {
			throw new SQLException("Can not retrieve query configuration ["
					+ configKey + "]!");
		}
		String dbName = queryConfig.getDbName();
		String tableName = queryConfig.getTableName();
		String tableIndexName = queryConfig.getIndexName();
		String[] queryColumns = queryConfig.getColumnNames();
		IHsc conn = getConnection();
		if (conn == null) {
			throw new SQLException("Can not make connection to database!");
		}
		try {
			return conn.insert(dbName, tableName, tableIndexName, queryColumns,
					values);
		} finally {
			releaseConnection(conn);
		}
	}

	/**
	 * Executes a UPDATE statement.
	 * 
	 * @param configKey
	 * @param values
	 * @param findValues
	 * @return
	 * @throws SQLException
	 */
	protected int execUpdate(final Object configKey, Object[] values,
			Object[] findValues) throws SQLException {
		QueryConfig queryConfig = buildQueryConfig(configKey);
		if (queryConfig == null) {
			throw new SQLException("Can not retrieve query configuration ["
					+ configKey + "]!");
		}
		String dbName = queryConfig.getDbName();
		String tableName = queryConfig.getTableName();
		String tableIndexName = queryConfig.getIndexName();
		String[] queryColumns = queryConfig.getColumnNames();
		IHsc conn = getConnection();
		if (conn == null) {
			throw new SQLException("Can not make connection to database!");
		}
		try {
			return conn.update(dbName, tableName, tableIndexName, queryColumns,
					values, findValues);
		} finally {
			releaseConnection(conn);
		}
	}

	protected int execDelete(final Object configKey, Object[] findValues)
			throws SQLException {
		QueryConfig queryConfig = buildQueryConfig(configKey);
		if (queryConfig == null) {
			throw new SQLException("Can not retrieve query configuration ["
					+ configKey + "]!");
		}
		String dbName = queryConfig.getDbName();
		String tableName = queryConfig.getTableName();
		String tableIndexName = queryConfig.getIndexName();
		String[] queryColumns = queryConfig.getColumnNames();
		IHsc conn = getConnection();
		if (conn == null) {
			throw new SQLException("Can not make connection to database!");
		}
		try {
			return conn.delete(dbName, tableName, tableIndexName, queryColumns,
					findValues);
		} finally {
			releaseConnection(conn);
		}
	}
}
