package ddth.dasp.framework.dbc;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.Statistics;

/**
 * This implementation of {@link IJdbcFactory} utilizes BoneCP as the connection
 * pooling back-end.
 * 
 * BoneCP homepape: http://jolbox.com/
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @since class available since v0.2.0
 */
public class BoneCpJdbcFactory extends AbstractJdbcFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(BoneCpJdbcFactory.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeDataSource(DataSource ds) throws SQLException {
        if (ds instanceof BoneCPDataSource) {
            ((BoneCPDataSource) ds).close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataSourceInfo internalGetDataSourceInfo(String name, DataSource ds) {
        DataSourceInfo dsInfo = internalGetDataSourceInfo(name);
        if (ds instanceof BoneCPDataSource) {
            BoneCPDataSource bonecpDs = (BoneCPDataSource) ds;
            try {
                // use reflection to get the internal pool
                // see http://forum.jolbox.com/viewtopic.php?f=3&t=310
                Field field = BoneCPDataSource.class.getDeclaredField("pool");
                field.setAccessible(true);
                BoneCP pool = (BoneCP) field.get(bonecpDs);
                Statistics stats = pool.getStatistics();
                int numActives = stats.getTotalLeased();
                int numIdles = stats.getTotalCreatedConnections() - numActives;
                dsInfo.setNumActives(numActives).setNumIdles(numIdles);
            } catch (Exception e) {
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
        BoneCPConfig bonecpConfig = new BoneCPConfig();
        bonecpConfig.setStatisticsEnabled(true);
        bonecpConfig.setAcquireRetryAttempts(2);
        bonecpConfig.setAcquireRetryDelay(5000, TimeUnit.MILLISECONDS);
        bonecpConfig.setConnectionTestStatement(getValidationQuery(driver));
        bonecpConfig.setConnectionTimeout(maxWaitTime, TimeUnit.MILLISECONDS);
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int numPartitions = Math.min(4, Math.max(2, numProcessors));
        int maxConnsPerPartition = maxActive / numPartitions > 1 ? maxActive / numPartitions : 1;
        int minConnsPerPartition = minIdle / numPartitions;
        bonecpConfig.setPartitionCount(numPartitions);
        bonecpConfig.setMaxConnectionsPerPartition(maxConnsPerPartition);
        bonecpConfig.setMinConnectionsPerPartition(minConnsPerPartition);
        bonecpConfig.setJdbcUrl(connUrl);
        bonecpConfig.setUsername(username);
        bonecpConfig.setPassword(password);
        BoneCPDataSource bonecpDs = new BoneCPDataSource(bonecpConfig);
        if (bonecpDs != null) {
            String dsName = calcHash(driver, connUrl, username, password, dbcpInfo);
            DataSourceInfo dsInfo = internalGetDataSourceInfo(dsName);
            dsInfo.setMaxActives(numPartitions * maxConnsPerPartition)
                    .setMaxIdles(numPartitions * maxConnsPerPartition).setMaxWait(maxWaitTime)
                    .setMinIdles(numPartitions * minConnsPerPartition);
        }
        return bonecpDs;
    }
}
