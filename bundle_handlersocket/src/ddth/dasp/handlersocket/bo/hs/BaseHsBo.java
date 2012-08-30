package ddth.dasp.handlersocket.bo.hs;

import java.sql.ResultSet;
import java.sql.SQLException;

import ddth.dasp.framework.bo.BaseBo;

/**
 * Use this class as starting point for Hnadlersocket-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseHsBo extends BaseBo implements IHsBo {
	@Override
	public void populate(ResultSet rs) throws SQLException {
	}
}
