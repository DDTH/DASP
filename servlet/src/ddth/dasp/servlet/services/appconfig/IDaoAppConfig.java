package ddth.dasp.servlet.services.appconfig;

public interface IDaoAppConfig {
	/**
	 * Loads an application config by key.
	 * 
	 * @param key
	 *            String
	 * @return IBoAppConfig
	 */
	public IBoAppConfig loadAppConfig(String key);
}
