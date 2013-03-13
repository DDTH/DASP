package ddth.dasp.framework.scheduletask;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.utils.OsgiUtils;

public abstract class AbstractBundleTask implements ITask, Serializable {

    private static final long serialVersionUID = "$Id: $".hashCode();
    private String filterQuery = null;
    private Map<String, String> filterMap = null;
    private String id = null;
    private boolean allowConcurrent = false;

    protected String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    protected Map<String, String> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.filterMap = filterMap;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void init() {
    }

    public void destroy() {
    }

    protected BundleContext getBundleContext() {
        return DaspGlobal.getOsgiBootstrap().getBundleContext();
    }

    protected ITaskRegistry getTaskRegistry() {
        if (!StringUtils.isBlank(filterQuery)) {
            return OsgiUtils.getService(getBundleContext(), ITaskRegistry.class, filterQuery);
        } else if (filterMap != null && filterMap.size() > 0) {
            return OsgiUtils.getService(getBundleContext(), ITaskRegistry.class, filterMap);
        } else {
            return OsgiUtils.getService(getBundleContext(), ITaskRegistry.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return StringUtils.isBlank(id) ? this.getClass().getName() : id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeTask(Object params) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            return internalExecuteTask(params);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Sub-class overrides this method to implement its own business.
     * 
     * @param params
     * @return
     */
    protected abstract Object internalExecuteTask(Object params);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllowConcurrent() {
        return allowConcurrent;
    }

    public void setAllowConcurrent(boolean allowConcurrent) {
        this.allowConcurrent = allowConcurrent;
    }
}
