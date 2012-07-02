package ddth.dasp.framework.bo;

import java.util.Map;

/**
 * Represents a Business Object (BO).
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public interface IBo {

    /**
     * Populates the BO with data from a Map, using mapping provided by
     * {@link #getDataMappings()}.
     * 
     * @param data
     */
    public void populate(Map<String, ?> data);

    /**
     * Maps external data fields with the BO's attributes.
     * 
     * @return Map<String, Object[]> the mapping in form of
     *         <code>{(String)external field name:[(String)BO's
     *         attribute name, (Class)attribute type]}</code>
     */
    public Map<String, Object[]> getDataMappings();
}
