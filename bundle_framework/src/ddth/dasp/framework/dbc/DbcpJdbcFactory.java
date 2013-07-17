package ddth.dasp.framework.dbc;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of {@link IJdbcFactory} utilizes Apache DBCP as the
 * connection pooling back-end.
 * 
 * Apache DBCP homepage: http://commons.apache.org/proper/commons-dbcp/
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class DbcpJdbcFactory extends AbstractJdbcFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(DbcpJdbcFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeDataSource(DataSource ds) throws SQLException {
        if (ds instanceof BasicDataSource) {
            ((BasicDataSource) ds).close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSourceInfo internalGetDataSourceInfo(String name, DataSource ds) {
        DataSourceInfo dsInfo = internalGetDataSourceInfo(name);
        if (ds instanceof BasicDataSource) {
            BasicDataSource basicDs = (BasicDataSource) ds;
            dsInfo.setNumActives(basicDs.getNumActive()).setNumIdles(basicDs.getNumIdle());
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
        BasicDataSource ds = new BasicDataSource();
        ds.setTestOnBorrow(true);
        int maxConnLifetime = (int) (getMaxConnectionLifetime() / 1000);
        if (maxConnLifetime > 0) {
            ds.setRemoveAbandoned(true);
            ds.setRemoveAbandonedTimeout(maxConnLifetime + 100);
        }
        // ds.setDriverClassName(driver);
        ds.setUrl(connUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setMaxWait(maxWaitTime);
        ds.setMinIdle(minIdle);
        String validationQuery = getValidationQuery(driver);
        if (!StringUtils.isBlank(validationQuery)) {
            ds.setValidationQuery(validationQuery);
            // PostgreSQL still not support the set query timeout method
            if (driver != null && !driver.contains("postgresql")) {
                // set the validation query timeout to 1 second
                ds.setValidationQueryTimeout(1);
            }
        }
        {
            String dsName = calcHash(driver, connUrl, username, password, dbcpInfo);
            DataSourceInfo dsInfo = internalGetDataSourceInfo(dsName);
            dsInfo.setMaxActives(maxActive).setMaxIdles(maxIdle).setMaxWait(maxWaitTime)
                    .setMinIdles(minIdle);
        }
        return ds;
    }
}
