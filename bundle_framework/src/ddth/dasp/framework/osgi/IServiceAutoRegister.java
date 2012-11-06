package ddth.dasp.framework.osgi;

import java.util.Properties;

/**
 * OSGi service implements this interface to be automatically registered.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface IServiceAutoRegister {
	public String getClassName();

	public Properties getProperties();
}
