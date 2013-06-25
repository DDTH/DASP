package ddth.dasp.servlet.runtime.osgi;

import java.util.Properties;

/**
 * Spring's bean as an OSGi's service.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public interface ISpringService {
	public String getServiceName();

	public Properties getServiceProperties();
}
