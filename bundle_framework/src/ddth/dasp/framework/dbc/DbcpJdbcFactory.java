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

    private final static Logger LOGGER = LoggerFactory.getLogger(DbcpJdbcFactory.class);

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
    protected DataSource buildDataSource(String driver, String connUrl, String username,
            String password) {
        return buildDataSource(driver, connUrl, username, password, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSource buildDataSource(String driver, String connUrl, String username,
            String password, DbcpInfo dbcpInfo) {
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
        BasicDataSource ds = new BasicDataSource();
        ds.setTestOnBorrow(true);
        int maxConnLifetime = (int) (getMaxConnectionLifetime() / 1000);
        if (maxConnLifetime > 0) {
            ds.setRemoveAbandoned(true);
            ds.setRemoveAbandonedTimeout(maxConnLifetime);
        }
        // CombinedClassLoader cl = new CombinedClassLoader();
        // cl.addLoader(DbcpJdbcFactory.class);
        // cl.addLoader(BasicDataSource.class);
        // ds.setDriverClassLoader(cl);
        ds.setDriverClassName(driver);
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
