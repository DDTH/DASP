package ddth.dasp.framework.bo.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC-based Business Object manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IJdbcBoManager {
    /**
     * Gets a database connection.
     * 
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException;

    /**
     * Releases an opening database connection.
     * 
     * @param conn
     * @throws SQLException
     */
    public void releaseConnection(Connection conn) throws SQLException;
}
