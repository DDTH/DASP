package ddth.dasp.framework.bo.jdbc;

import java.util.HashMap;
import java.util.Map;

public class SqlProps implements Cloneable {

    @Override
    public SqlProps clone() {
        try {
            SqlProps obj = (SqlProps) super.clone();
            obj.populate(this.props);
            return obj;
        } catch (CloneNotSupportedException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> props = new HashMap<String, Object>();

    public void populate(Map<String, Object> props) {
        this.props.putAll(props);
    }

    /**
     * Gets the sql query.
     * 
     * @return
     */
    public String getSql() {
        Object value = props.get("sql");
        return value != null ? value.toString() : null;
    }

    /**
     * Sets the sql query.
     * 
     * @param sql
     */
    public void setSql(String sql) {
        props.put("sql", sql);
    }
}
