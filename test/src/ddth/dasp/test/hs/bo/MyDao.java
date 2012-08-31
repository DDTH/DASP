package ddth.dasp.test.hs.bo;

import java.sql.SQLException;
import java.util.Map;

import ddth.dasp.handlersocket.bo.hs.BaseHsBoManager;

public class MyDao extends BaseHsBoManager implements IMyDao {
	public Map<String, Object>[] getRows() throws SQLException {
		final String configKey = "getRows";
		return execSelect(configKey, null);
	}

	public boolean createRow(Object[] values) throws SQLException {
		final String configKey = "createRow";
		return execInsert(configKey, values);
	}
}
