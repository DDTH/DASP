package ddth.dasp.framework.bo;

import java.sql.Connection;
import java.sql.SQLException;

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
