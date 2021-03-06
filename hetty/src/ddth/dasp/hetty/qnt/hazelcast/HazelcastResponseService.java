package ddth.dasp.hetty.qnt.hazelcast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.hazelcastex.IHazelcastClient;
import ddth.dasp.common.hazelcastex.IHazelcastClientFactory;
import ddth.dasp.common.hazelcastex.IMessageListener;
import ddth.dasp.common.hazelcastex.PoolConfig;
import ddth.dasp.hetty.front.AbstractHettyResponseService;
import ddth.dasp.hetty.message.IMessageFactory;
import ddth.dasp.hetty.message.IResponse;

/*
 * TODO: what would happen if a message comes when onMessage() is busy?
 */
public class HazelcastResponseService extends AbstractHettyResponseService implements
        IMessageListener<Object> {

    private IHazelcastClientFactory hazelcastClientFactory;
    private List<String> hazelcastServers;
    private String hazelcastUsername, hazelcastPassword;
    private PoolConfig poolConfig;
    private IMessageFactory messageFactory;
    private String topicName;
    private boolean _listening = false, _destroyed = false;

    protected String getTopicName() {
        return topicName;
    }

    public HazelcastResponseService setTopicName(String topicName) {
        this.topicName = topicName;
        return this;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public HazelcastResponseService setHazelcastClientFactory(
            IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
        return this;
    }

    protected List<String> getHazelcastServers() {
        return hazelcastServers;
    }

    public HazelcastResponseService setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
        return this;
    }

    protected String getHazelcastUsername() {
        return hazelcastUsername;
    }

    public HazelcastResponseService setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
        return this;
    }

    protected String getHazelcastPassword() {
        return hazelcastPassword;
    }

    public HazelcastResponseService setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
        return this;
    }

    protected PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public HazelcastResponseService setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    protected IMessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(IMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    private class MessageListenerKeeper implements Runnable {
        private IMessageListener<Object> messageListener;

        public MessageListenerKeeper(IMessageListener<Object> messageListener) {
            this.messageListener = messageListener;
        }

        private void ping() {
            try {
                if (!_hazelcastClient.ping()) {
                    unsubscribe();
                }
            } catch (Exception e) {
                _listening = false;
                returnHazelcastClient();
            }
        }

        @Override
        public void run() {
            if (!_destroyed) {
                try {
                    if (_listening) {
                        ping();
                    } else {
                        IHazelcastClient hazelcastClient = getHazelcastClient();
                        if (hazelcastClient != null) {
                            _listening = true;
                            hazelcastClient.subscribe(topicName, messageListener);
                        }
                    }
                } finally {
                    DaspGlobal.getScheduler().schedule(this, 5000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public void init() {
        Runnable command = new MessageListenerKeeper(this);
        DaspGlobal.getScheduler().schedule(command, 2000, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        _destroyed = true;
        try {
            unsubscribe();
        } finally {
            returnHazelcastClient();
        }
    }

    private IHazelcastClient _hazelcastClient;

    synchronized private IHazelcastClient getHazelcastClient() {
        if (_hazelcastClient == null) {
            _hazelcastClient = hazelcastClientFactory.getHazelcastClient(hazelcastServers,
                    hazelcastUsername, hazelcastPassword, poolConfig);
        }
        return _hazelcastClient;
    }

    synchronized private void returnHazelcastClient() {
        try {
            hazelcastClientFactory.returnHazelcastClient(_hazelcastClient);
        } finally {
            _hazelcastClient = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe() {
        try {
            if (_hazelcastClient != null) {
                _hazelcastClient.unsubscribe(topicName, this);
            }
        } finally {
            _listening = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Object message) {
        IResponse response = null;
        if (message instanceof byte[]) {
            response = messageFactory.deserializeResponse((byte[]) message);
        } else if (message instanceof IResponse) {
            response = (IResponse) message;
        }
        writeResponse(response);
    }
}
