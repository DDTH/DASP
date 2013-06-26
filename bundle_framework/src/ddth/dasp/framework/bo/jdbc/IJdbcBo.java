package ddth.dasp.framework.bo.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import ddth.dasp.framework.bo.IBo;

/**
 * JDBC-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IJdbcBo extends IBo {
    /**
     * Populates the BO from a {@link ResultSet} using the mapping provided by
     * {@link #getDataMappings()}.
     * 
     * @param rs
     * @throws
     */
    public void populate(ResultSet rs) throws SQLException;

    /**
     * Populates the BO from a {@link ResultSet} using the mapping provided by
     * {@link #getDataMappings()}.
     * 
     * @param rs
     * @param rsMetaData
     * @throws
     */
    public void populate(ResultSet rs, ResultSetMetaData rsMetaData) throws SQLException;
}
