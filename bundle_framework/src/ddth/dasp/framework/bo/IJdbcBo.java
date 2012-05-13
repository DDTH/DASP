package ddth.dasp.framework.bo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * JDBC-based business object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IJdbcBo {
    /**
     * Maps database table columns to BO's attributes.
     * 
     * @return Map<String, Object[]> {(String)db field name:[(String)BO's
     *         attribute name, (Class)attribute type]}
     */
    public Map<String, Object[]> getFieldMap();

    /**
     * Populates the BO from a {@link ResultSet} using the mapping probided by
     * {@link #getFieldMap()}.
     * 
     * @param rs
     *            ResultSet
     * @throws SQLException
     */
    public void populate(ResultSet rs) throws SQLException;
}
