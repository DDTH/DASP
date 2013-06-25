package ddth.dasp.common.hazelcastex.impl;

import java.util.List;

import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientPool;

public abstract class AbstractHazelcastClient implements IHazelcastClient {

    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;
    private IHazelcastClientPool hazelcastClientPool;

    protected IHazelcastClientPool getHazelcastClientPool() {
        return hazelcastClientPool;
    }

    public AbstractHazelcastClient setHazelcastClientPool(IHazelcastClientPool hazelcastClientPool) {
        this.hazelcastClientPool = hazelcastClientPool;
        return this;
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public AbstractHazelcastClient setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public AbstractHazelcastClient setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public AbstractHazelcastClient setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }
}
