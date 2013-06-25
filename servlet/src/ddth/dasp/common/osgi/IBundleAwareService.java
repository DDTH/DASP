package ddth.dasp.common.osgi;

import org.osgi.framework.Bundle;

/**
 * Implements this interface to indicate that a service is bundle-aware.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IBundleAwareService {
    public void setBundle(Bundle bundle);
}
