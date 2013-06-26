package ddth.dasp.framework.dbc;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Captures information of an opening JDBC connection.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * 
 */
public class OpenConnectionInfo {

    private static final AtomicLong COUNTER = new AtomicLong();

    private long id = COUNTER.incrementAndGet();
    private long creationTimestamp = System.currentTimeMillis();
    private String datasourceKey;

    public OpenConnectionInfo(String datasourceKey) {
        this.datasourceKey = datasourceKey;
    }

    public long getId() {
        return id;
    }

    public String getDatasourceKey() {
        return datasourceKey;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public long getLifetime() {
        return System.currentTimeMillis() - creationTimestamp;
    }
}
