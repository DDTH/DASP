package ddth.dasp.servlet.logging;

/**
 * Request log engine APIs.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IRequestLogEngine {
	/**
	 * Logs a request.
	 * 
	 * @param entry
	 *            RequestLogEntry
	 */
	public void log(RequestLogEntry entry);
}
