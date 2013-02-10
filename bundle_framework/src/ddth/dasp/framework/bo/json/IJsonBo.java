package ddth.dasp.framework.bo.json;

import ddth.dasp.framework.bo.IBo;

/**
 * Json-based Business Object.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IJsonBo extends IBo {
    /**
     * Populates the BO with data from a Json string, using mapping provided by
     * {@link #getDataMappings()}.
     * 
     * @param jsonString
     */
    public void populate(String jsonString);

    /**
     * Serializes the BO to JSON string.
     * 
     * @return
     */
    public String toJson();
}
