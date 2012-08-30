package ddth.dasp.handlersocket.bo.hs;

import java.sql.ResultSet;
import java.sql.SQLException;

import ddth.dasp.framework.bo.IBo;

/**
 * Handlersocket-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IHsBo extends IBo {
	/**
	 * Populates the BO from a {@link ResultSet} using the mapping provided by
	 * {@link #getDataMappings()}.
	 * 
	 * @param rs
	 * @throws
	 */
	public void populate(ResultSet rs) throws SQLException;
}
