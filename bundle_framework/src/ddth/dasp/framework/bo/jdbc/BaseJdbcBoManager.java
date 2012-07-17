package ddth.dasp.framework.bo.jdbc;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

import ddth.dasp.common.logging.JdbcLogEntry;
import ddth.dasp.common.logging.JdbcLogger;
import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.common.utils.PropsUtils;
import ddth.dasp.framework.bo.CachedBoManager;
import ddth.dasp.framework.dbc.IJdbcFactory;
import ddth.dasp.framework.dbc.JdbcUtils;
import ddth.dasp.framework.utils.EhProperties;

/**
 * Use this class as starting point for JDBC-based Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseJdbcBoManager extends CachedBoManager implements IJdbcBoManager {

    private final static int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private Logger LOGGER = LoggerFactory.getLogger(BaseJdbcBoManager.class);
    private IJdbcFactory jdbcFactory;
    private String dbDriver, dbConnUrl, dbUsername, dbPassword;
    private List<String> setupSqls;
    private Properties sqlProps = new EhProperties();
    private ConcurrentMap<String, SqlProps> cacheSqlProps = new MapMaker().concurrencyLevel(
            NUM_PROCESSORS).makeMap();
    private Object sqlPropsLocation;

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

    public List<String> getSetupSqls() {
        return setupSqls;
    }

    public void setSetupSqls(List<String> setupSqls) {
        this.setupSqls = setupSqls;
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
            Properties props = PropsUtils.loadProperties((InputStream) sqlProps);
            if (props != null) {
                this.sqlProps.putAll(props);
            }
        } else if (sqlProps != null) {
            String location = sqlProps.toString();
            InputStream is = getClass().getResourceAsStream(location);
            Properties props = PropsUtils.loadProperties(is, location.endsWith(".xml"));
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
    protected Object getSqlPropsLocation() {
        return sqlPropsLocation;
    }

    /**
     * Sets the SQL properties location. The location can be either of:
     * 
     * <ul>
     * <li>{@link InputStream}: properties are loaded from the input stream.</li>
     * <li>{@link Properties}: properties are copied from this one.</li>
     * <li>{@link String}: properties are loaded from file (located within the
     * classpath) specified by this string.</li>
     * </ul>
     * 
     * @param sqlPropsLocation
     */
    public void setSqlPropsLocation(Object sqlPropsLocation) {
        this.sqlPropsLocation = sqlPropsLocation;
    }

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
                    Map<String, Object> props = JsonUtils.fromJson(rawProps, Map.class);
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
     * Runs setup SQLs for newly obtained {@link Connection}.
     * 
     * @param conn
     * @throws SQLException
     */
    protected void runSetupSqls(Connection conn) throws SQLException {
        if (setupSqls != null && setupSqls.size() > 0) {
            Statement stm = conn.createStatement();
            try {
                for (String sql : setupSqls) {
                    stm.execute(sql);
                }
            } finally {
                stm.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = jdbcFactory.getConnection(dbDriver, dbConnUrl, dbUsername, dbPassword);
        if (conn != null) {
            runSetupSqls(conn);
        }
        return conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseConnection(Connection conn) throws SQLException {
        jdbcFactory.releaseConnection(conn);
    }

    /**
     * Executes a COUNT query and returns the result.
     * 
     * @param sqlKey
     * @param params
     * @return
     * @throws SQLException
     */
    protected Long executeCount(final String sqlKey, Map<String, Object> params)
            throws SQLException {
        return executeCount(sqlKey, params, null);
    }

    /**
     * Executes a COUNT query and returns the result.
     * 
     * @param sqlKey
     * @param params
     * @return
     * @throws SQLException
     */
    protected Long executeCount(final String sqlKey, Map<String, Object> params,
            final String cacheKey) throws SQLException {
        Long result = null;
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // get from cache
            result = (Long) getFromCache(cacheKey);
        }
        if (result == null) {
            // cache missed
            SqlProps sqlProps = getSqlProps(sqlKey);
            if (sqlProps == null) {
                throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
            }
            Connection conn = getConnection();
            PreparedStatement stm = null;
            ResultSet rs = null;
            if (conn == null) {
                throw new RuntimeException("Can not make connection to database!");
            }
            try {
                long startTimestamp = System.currentTimeMillis();
                String sql = sqlProps.getSql();
                stm = JdbcUtils.prepareStatement(conn, sql, params);
                rs = stm.executeQuery();
                if (rs.next()) {
                    result = rs.getLong(1);
                }
                long endTimestamp = System.currentTimeMillis();
                JdbcLogEntry jdbcLogEntry = new JdbcLogEntry(startTimestamp, endTimestamp, sql,
                        params);
                JdbcLogger.log(jdbcLogEntry);
            } finally {
                JdbcUtils.closeResources(null, stm, rs);
                releaseConnection(conn);
            }
        }
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // put to cache
            putToCache(cacheKey, result);
        }
        return result;
    }

    /**
     * Executes a non-SELECT query and returns the number of affected rows.
     * 
     * @param sqlKey
     * @param params
     * @return
     * @throws SQLException
     */
    protected long execNonSelect(final String sqlKey, Map<String, Object> params)
            throws SQLException {
        SqlProps sqlProps = getSqlProps(sqlKey);
        if (sqlProps == null) {
            throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
        }
        Connection conn = getConnection();
        PreparedStatement stm = null;
        if (conn == null) {
            throw new RuntimeException("Can not make connection to database!");
        }
        try {
            long startTimestamp = System.currentTimeMillis();
            String sql = sqlProps.getSql();
            stm = JdbcUtils.prepareStatement(conn, sql, params);
            long result = stm.executeUpdate();
            long endTimestamp = System.currentTimeMillis();
            JdbcLogEntry jdbcLogEntry = new JdbcLogEntry(startTimestamp, endTimestamp, sql, params);
            JdbcLogger.log(jdbcLogEntry);
            return result;
        } finally {
            JdbcUtils.closeResources(null, stm, null);
            releaseConnection(conn);
        }
    }

    /**
     * Executes a SELECT query and returns the result as an array of records,
     * each record is a Map<String, Object>.
     * 
     * @param sqlKey
     * @param params
     * @return
     * @throws SQLException
     */
    protected Map<String, Object>[] executeSelect(final String sqlKey, Map<String, Object> params)
            throws SQLException {
        return executeSelect(sqlKey, params, null);
    }

    /**
     * Executes a SELECT query and returns the result as an array of records,
     * each record is a Map<String, Object>.
     * 
     * @param sqlKey
     * @param params
     * @param cacheKey
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object>[] executeSelect(final String sqlKey, Map<String, Object> params,
            final String cacheKey) throws SQLException {
        List<Map<String, Object>> result = null;
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // get from cache
            result = (List<Map<String, Object>>) getFromCache(cacheKey);
        }
        if (result == null) {
            // cache missed
            SqlProps sqlProps = getSqlProps(sqlKey);
            if (sqlProps == null) {
                throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
            }
            Connection conn = getConnection();
            PreparedStatement stm = null;
            ResultSet rs = null;
            if (conn == null) {
                throw new RuntimeException("Can not make connection to database!");
            }
            try {
                long startTimestamp = System.currentTimeMillis();
                String sql = sqlProps.getSql();
                result = new ArrayList<Map<String, Object>>();
                stm = JdbcUtils.prepareStatement(conn, sql, params);
                rs = stm.executeQuery();
                ResultSetMetaData rsMetaData = rs != null ? rs.getMetaData() : null;
                while (rs.next()) {
                    Map<String, Object> obj = new HashMap<String, Object>();
                    for (int i = 1, n = rsMetaData.getColumnCount(); i <= n; i++) {
                        String colName = rsMetaData.getColumnName(i);
                        Object value = rs.getObject(colName);
                        obj.put(colName, value);
                    }
                    result.add(obj);
                }
                long endTimestamp = System.currentTimeMillis();
                JdbcLogEntry jdbcLogEntry = new JdbcLogEntry(startTimestamp, endTimestamp, sql,
                        params);
                JdbcLogger.log(jdbcLogEntry);
            } finally {
                JdbcUtils.closeResources(null, stm, rs);
                releaseConnection(conn);
            }
        }
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // put to cache
            putToCache(cacheKey, result);
        }
        return result.toArray((Map<String, Object>[]) Array.newInstance(Map.class, 0));
    }

    /**
     * Executes a SELECT query and returns the result as an array of result,
     * each result is an instance of type {@link BaseJdbcBo}.
     * 
     * @param <T>
     * @param sqlKey
     * @param params
     * @param clazz
     * @return
     * @throws SQLException
     */
    protected <T extends BaseJdbcBo> T[] executeSelect(final String sqlKey,
            Map<String, Object> params, Class<T> clazz) throws SQLException {
        return executeSelect(sqlKey, params, clazz, null);
    }

    /**
     * Executes a SELECT query and returns the result as an array of result,
     * each result is an instance of type {@link BaseJdbcBo}.
     * 
     * @param <T>
     * @param sqlKey
     * @param params
     * @param clazz
     * @param cacheKey
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseJdbcBo> T[] executeSelect(final String sqlKey,
            Map<String, Object> params, Class<T> clazz, final String cacheKey) throws SQLException {
        List<T> result = null;
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // get from cache
            result = (List<T>) getFromCache(cacheKey);
        }
        if (result == null) {
            // cache missed
            SqlProps sqlProps = getSqlProps(sqlKey);
            if (sqlProps == null) {
                throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
            }
            Connection conn = getConnection();
            PreparedStatement stm = null;
            ResultSet rs = null;
            if (conn == null) {
                throw new RuntimeException("Can not make connection to database!");
            }
            try {
                long startTimestamp = System.currentTimeMillis();
                String sql = sqlProps.getSql();
                result = new ArrayList<T>();
                stm = JdbcUtils.prepareStatement(conn, sql, params);
                rs = stm.executeQuery();
                ResultSetMetaData rsMetaData = rs != null ? rs.getMetaData() : null;
                while (rs.next()) {
                    T obj = null;
                    try {
                        obj = createBusinessObject(clazz);
                        obj.populate(rs, rsMetaData);
                        result.add(obj);
                    } catch (Exception e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        } else {
                            throw new SQLException(e);
                        }
                    }
                }
                long endTimestamp = System.currentTimeMillis();
                JdbcLogEntry jdbcLogEntry = new JdbcLogEntry(startTimestamp, endTimestamp, sql,
                        params);
                JdbcLogger.log(jdbcLogEntry);
            } finally {
                JdbcUtils.closeResources(null, stm, rs);
                releaseConnection(conn);
            }
        }
        if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
            // put to cache
            putToCache(cacheKey, result);
        }
        return result.toArray((T[]) Array.newInstance(clazz, 0));
    }
}
