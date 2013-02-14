package ddth.dasp.framework.cache.hazelcast;

import java.util.List;

import ddth.dasp.common.hazelcast.HazelcastClientFactory;
import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.framework.cache.AbstractCacheManager;
import ddth.dasp.framework.cache.ICacheManager;

/**
 * <a href="http://www.hazelcast.com/">Hazelcast</a> implementation of
 * {@link ICacheManager}.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 * @version 0.1.0
 */
public class HazelcastCacheManager extends AbstractCacheManager {

    private IHazelcastClientFactory hazelcastClientFactory;
    private boolean myOwnHazelcastClientFactory = false;
    private String hazelcastUsername, hazelcastPassword;
    private List<String> hazelcastServers;

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public void setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
    }

    public void setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
    }

    public void setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        super.init();
        if (hazelcastClientFactory == null) {
            HazelcastClientFactory hzcf = new HazelcastClientFactory();
            hzcf.setHazelcastUsername(hazelcastUsername);
            hzcf.setHazelcastPassword(hazelcastPassword);
            hzcf.setHazelcastServers(hazelcastServers);
            hzcf.init();
            hazelcastClientFactory = hzcf;
            myOwnHazelcastClientFactory = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (myOwnHazelcastClientFactory) {
            ((HazelcastClientFactory) hazelcastClientFactory).destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HazelcastCache createCacheInternal(String name, long capacity, long expireAfterWrite,
            long expireAfterAccess) {
        HazelcastCache cache = new HazelcastCache(hazelcastClientFactory, name);
        cache.setCapacity(capacity > 0 ? capacity : getDefaultCacheCapacity());
        cache.setExpireAfterAccess(expireAfterAccess);
        cache.setExpireAfterWrite(expireAfterWrite);
        cache.init();
        return cache;
    }
}
