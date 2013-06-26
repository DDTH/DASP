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
     * Checks if we are currently in middle of a transaction
     * 
     * @return
     */
    public boolean inTransaction();

    /**
     * Starts a database transaction with default transaction level.
     * 
     * @return
     * @throws SQLException
     */
    public Connection startTransaction() throws SQLException;

    /**
     * Starts a database transaction.
     * 
     * @param transactionIsolationLevel
     * @return
     * @throws SQLException
     */
    public Connection startTransaction(int transactionIsolationLevel) throws SQLException;

    /**
     * Cancels/Rolls back a database transaction.
     * 
     * @return
     */
    public void cancelTransaction() throws SQLException;

    /**
     * Finishes/Commits a database transaction.
     * 
     * @return
     * @throws SQLException
     */
    public void finishTransaction() throws SQLException;

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
