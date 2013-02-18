package ddth.dasp.framework.logging.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ddth.dasp.common.logging.ILogWriter;
import ddth.dasp.framework.dbc.IJdbcFactory;

/**
 * This implementation of {@link ILogWriter} writes log to database table using
 * JDBC.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public abstract class JdbcLogWriter implements ILogWriter {

    private IJdbcFactory jdbcFactory;
    // private JdbcTemplate jdbcTemplate;
    private String dbDriver, dbConnUrl, dbUsername, dbPassword;

    /**
     * Initializing method.
     */
    public void init() {
        // DataSource ds = jdbcFactory.getDataSource(dbDriver, dbConnUrl,
        // dbUsername, dbPassword);
        // jdbcTemplate = new JdbcTemplate(ds);
    }

    /**
     * Destruction method.
     */
    public void destroy() {
        // EMPTY
    }

    protected IJdbcFactory getJdbcFactory() {
        return jdbcFactory;
    }

    public void setJdbcFactory(IJdbcFactory jdbcFactory) {
        this.jdbcFactory = jdbcFactory;
    }

    // protected JdbcTemplate getJdbcTemplate() {
    // return jdbcTemplate;
    // }

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

    /**
     * Gets a DB connection.
     * 
     * @return String
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        return jdbcFactory.getConnection(dbDriver, dbConnUrl, dbUsername, dbPassword);
    }

    /**
     * Writes log to the specified database table.
     * 
     * @param dbTable
     *            String name of the database table
     * @param logData
     *            Map the log data to be written
     * @throws SQLException
     */
    protected void writeLog(String dbTable, Map<String, Object> logData) throws SQLException {
        if (logData == null || logData.size() == 0) {
            return;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(dbTable);
        sql.append("(");
        List<Object> values = new ArrayList<Object>(logData.size());
        for (Entry<String, Object> entry : logData.entrySet()) {
            String column = entry.getKey();
            Object value = entry.getValue();
            sql.append(column).append(",");
            values.add(value);
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")VALUES(");
        sql.append("?,");
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")");

        // jdbcTemplate.update(sql.toString(), values.toArray());
    }
}
