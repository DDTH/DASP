package ddth.dasp.test.hs.bo;

import java.sql.SQLException;
import java.util.Map;

public interface IMyDao {
	public Map<String, Object>[] getRows() throws SQLException;
	
	public boolean createRow(Object[] values) throws SQLException;
}
