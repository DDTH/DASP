package ddth.dasp.framework.dbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * This implementation of {@link IJdbcFactory} utilizes C3P0 as the connection
 * pooling back-end.
 * 
 * C3P0 homepage: http://www.mchange.com/projects/c3p0/
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @since class available since v0.2.0
 */
public class C3p0JdbcFactory extends AbstractJdbcFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(C3p0JdbcFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeDataSource(DataSource ds) throws SQLException {
        DataSources.destroy(ds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSourceInfo internalGetDataSourceInfo(String name, DataSource ds) {
        DataSourceInfo dsInfo = internalGetDataSourceInfo(name);
        if (ds instanceof PooledDataSource) {
            PooledDataSource pooledDs = (PooledDataSource) ds;
            try {
                dsInfo.setNumActives(pooledDs.getNumBusyConnectionsAllUsers()).setNumIdles(
                        pooledDs.getNumIdleConnectionsAllUsers());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return dsInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSource buildDataSource(String driver, String connUrl, String username,
            String password) throws SQLException {
        return buildDataSource(driver, connUrl, username, password, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSource buildDataSource(String driver, String connUrl, String username,
            String password, DbcpInfo dbcpInfo) throws SQLException {
        int maxActive = dbcpInfo != null ? dbcpInfo.getMaxActive() : DbcpInfo.DEFAULT_MAX_ACTIVE;
        long maxWaitTime = dbcpInfo != null ? dbcpInfo.getMaxWaitTime()
                : DbcpInfo.DEFAULT_MAX_WAIT_TIME;
        int maxIdle = dbcpInfo != null ? dbcpInfo.getMaxIdle() : DbcpInfo.DEFAULT_MAX_IDLE;
        int minIdle = dbcpInfo != null ? dbcpInfo.getMinIdle() : DbcpInfo.DEFAULT_MIN_IDLE;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building a datasource {driver:" + driver + ";connUrl:" + connUrl
                    + ";username:" + username + ";maxActive:" + maxActive + ";maxWait:"
                    + maxWaitTime + ";minIdle:" + minIdle + ";maxIdle:" + maxIdle + "}...");
        }
        try {
            /*
             * Note: we load the driver class here!
             */
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        Map<String, Object> overrideProps = new HashMap<String, Object>();
        overrideProps.put("maxIdleTimeExcessConnections", 300); // seconds
        overrideProps.put("acquireRetryAttempts", 2);
        overrideProps.put("checkoutTimeout", maxWaitTime);// millisecs
        // overrideProps.put("idleConnectionTestPeriod", 10); // seconds
        // overrideProps.put("maxStatements", 0);
        overrideProps.put("maxPoolSize", maxActive);
        overrideProps.put("minPoolSize", minIdle);
        overrideProps.put("testConnectionOnCheckout", true);
        overrideProps.put("preferredTestQuery", getValidationQuery(driver));
        // ClassLoader oldCladdLoader =
        // Thread.currentThread().getContextClassLoader();
        // CombinedClassLoader newClassLoader = new CombinedClassLoader();
        // newClassLoader.addLoader(oldCladdLoader);
        // newClassLoader.addLoader(C3p0JdbcFactory.class);
        // newClassLoader.addLoader(DataSources.class);
        // Thread.currentThread().setContextClassLoader(newClassLoader);
        // try {
        DataSource unpooledDs = DataSources.unpooledDataSource(connUrl, username, password);
        PooledDataSource pooledDs = (PooledDataSource) DataSources.pooledDataSource(unpooledDs,
                overrideProps);
        if (pooledDs != null) {
            String dsName = calcHash(driver, connUrl, username, password, dbcpInfo);
            DataSourceInfo dsInfo = internalGetDataSourceInfo(dsName);
            dsInfo.setMaxActives(maxActive).setMaxIdles(maxIdle).setMaxWait(maxWaitTime)
                    .setMinIdles(minIdle);
        }
        return pooledDs;
        // } finally {
        // Thread.currentThread().setContextClassLoader(oldCladdLoader);
        // }
    }
}
