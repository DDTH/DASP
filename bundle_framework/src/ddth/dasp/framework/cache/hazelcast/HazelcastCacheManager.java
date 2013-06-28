package ddth.dasp.framework.cache.hazelcast;

import java.util.List;

import org.osgi.framework.BundleContext;

import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.impl.HazelcastClientFactory;
import ddth.dasp.common.utils.OsgiUtils;
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

    private boolean myOwnHazelcastClientFactory = false;
    private IHazelcastClientFactory hazelcastClientFactory;
    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;

    public IHazelcastClientFactory getHazelcastClientFactory() {
        if (hazelcastClientFactory != null) {
            return hazelcastClientFactory;
        }
        /*
         * If the Hazelcast client factory has not been set, try to get it from
         * OSGi container.
         */
        BundleContext bundleContext = getBundleContext();
        if (bundleContext != null) {
            IHazelcastClientFactory _hazelcastClientFactory = OsgiUtils.getService(bundleContext,
                    IHazelcastClientFactory.class);
            if (_hazelcastClientFactory != null) {
                return _hazelcastClientFactory;
            }
        }
        /*
         * Build an instance of Hazelcast client factory for my own.
         */
        synchronized (this) {
            // safeguard check
            if (hazelcastClientFactory == null) {
                HazelcastClientFactory hzcf = new HazelcastClientFactory();
                hzcf.init();
                hazelcastClientFactory = hzcf;
                myOwnHazelcastClientFactory = true;
            }
        }
        return hazelcastClientFactory;
    }

    public HazelcastCacheManager setHazelcastClientFactory(
            IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public HazelcastCacheManager setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public HazelcastCacheManager setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }

    public List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public HazelcastCacheManager setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        try {
            if (myOwnHazelcastClientFactory && hazelcastClientFactory != null) {
                ((HazelcastClientFactory) hazelcastClientFactory).destroy();
            }
        } finally {
            super.destroy();
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

        cache.setHazelcastServer(hazelcastServers).setHazelcastUsername(hazelcastUsername)
                .setHazelcastPassword(hazelcastPassword);

        cache.init();
        return cache;
    }
}
