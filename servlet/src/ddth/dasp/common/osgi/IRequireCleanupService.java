package ddth.dasp.common.osgi;

/**
 * Implement this interface to indicate that service needs to be clean up upon
 * unregistering.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IRequireCleanupService {
    /**
     * Called to perform cleaning up tasks.
     */
    public void destroy();
}
