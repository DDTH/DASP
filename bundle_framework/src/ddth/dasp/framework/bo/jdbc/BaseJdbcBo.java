package ddth.dasp.framework.bo.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ddth.dasp.framework.bo.BaseBo;

/**
 * Use this class as starting point for JDBC-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseJdbcBo extends BaseBo implements IJdbcBo {

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(ResultSet rs) throws SQLException {
        populate(rs, rs.getMetaData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(ResultSet rs, ResultSetMetaData rsMetaData) throws SQLException {
        Map<String, Object> data = new HashMap<String, Object>();
        for (int i = 1, n = rsMetaData.getColumnCount(); i <= n; i++) {
            String colLabel = rsMetaData.getColumnLabel(i);
            if (StringUtils.isEmpty(colLabel)) {
                colLabel = rsMetaData.getColumnName(i);
            }
            Object value = rs.getObject(colLabel);
            data.put(colLabel, value);
        }
        populate(data);
    }
}
