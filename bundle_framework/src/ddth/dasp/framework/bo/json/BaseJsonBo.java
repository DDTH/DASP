package ddth.dasp.framework.bo.json;

import java.util.HashMap;
import java.util.Map;

import ddth.dasp.common.utils.JsonUtils;
import ddth.dasp.framework.bo.BaseBo;

/**
 * Use this class as starting point for JSON-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public abstract class BaseJsonBo extends BaseBo implements IJsonBo {

    private Map<String, Object> attrs = new HashMap<String, Object>();

    protected Object getAttribute(String name) {
        return attrs.get(name);
    }

    protected void setAttribute(String name, Object value) {
        attrs.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toJson() {
        return JsonUtils.toJson(attrs);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void populate(String jsonString) {
        Map<String, Object> map = JsonUtils.fromJson(jsonString, Map.class);
        populate(map);
    }

}
