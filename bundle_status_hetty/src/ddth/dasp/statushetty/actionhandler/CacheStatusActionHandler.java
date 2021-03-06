package ddth.dasp.statushetty.actionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.framework.cache.ICacheManager;
import ddth.dasp.framework.cache.hazelcast.HazelcastCacheManager;
import ddth.dasp.hetty.message.IRequest;

public class CacheStatusActionHandler extends BaseActionHandler {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> buildViewModel(IRequest request) {
        Map<String, Object> model = super.buildViewModel(request);
        model.put("CACHE", buildModelCache());
        return model;
    }

    @SuppressWarnings("unchecked")
    private Object buildModelCache() {
        List<Object> model = new ArrayList<Object>();
        Object temp = DaspGlobal.getGlobalVar(ICacheManager.GLOBAL_KEY);
        if (!(temp instanceof Map)) {
            temp = new HashMap<String, ICacheManager>();
        }
        Map<String, ICacheManager> allCacheManagers = (Map<String, ICacheManager>) temp;
        Map<String, Object> modelEntry;
        for (Entry<String, ICacheManager> entry : allCacheManagers.entrySet()) {
            ICacheManager cacheManager = entry.getValue();
            modelEntry = new HashMap<String, Object>();
            modelEntry.put("id", entry.getKey());
            modelEntry.put("cacheManager", cacheManager);
            if (cacheManager instanceof HazelcastCacheManager) {
                List<String> servers = ((HazelcastCacheManager) cacheManager).getHazelcastServers();
                modelEntry.put("cacheServer", servers != null ? servers.toString() : "null");
            } else {
                modelEntry.put("cacheServer", "-");
            }
            model.add(modelEntry);
        }
        return model;
    }
}
