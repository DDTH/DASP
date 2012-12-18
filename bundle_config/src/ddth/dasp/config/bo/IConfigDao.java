package ddth.dasp.config.bo;

import org.osgi.framework.BundleContext;

public interface IConfigDao {

    public void destroy(BundleContext bundleContext);

    public void init(BundleContext bundleContext);

    public void setConfig(String module, String key, Object value);

    public Object getConfig(String module, String key);

    public Boolean getConfigAsBoolean(String module, String key);

    public Double getConfigAsDouble(String module, String key);

    public Float getConfigAsFloat(String module, String key);

    public Integer getConfigAsInteger(String module, String key);

    public Long getConfigAsLong(String module, String key);

    public String getConfigAsString(String module, String key);
}
