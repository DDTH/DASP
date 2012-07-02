package ddth.dasp.framework.bo.jdbc;

import java.util.HashMap;
import java.util.Map;

public class SqlProps {

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
}
