package ddth.dasp.framework.osgi;

import java.util.Properties;

public class ServiceInfo {
    private String className;
    private Object service;
    private Properties properties = new Properties();

    public ServiceInfo() {
    }

    public ServiceInfo(String className, Object service) {
        setClassName(className);
        setService(service);
    }

    public ServiceInfo(String name, Object service, Properties props) {
        setClassName(name);
        setService(service);
        setProperties(props);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        if (properties != null) {
            this.properties.clear();
            this.properties.putAll(properties);
        }
    }
}
