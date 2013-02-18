package ddth.dasp.hetty.qnt.hazelcast;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.hazelcast.IHazelcastClientFactory;
import ddth.dasp.hetty.front.AbstractHettyResponseService;
import ddth.dasp.hetty.message.protobuf.HettyProtoBuf;

public class HazelcastResponseService extends AbstractHettyResponseService implements
        MessageListener<HettyProtoBuf.Response> {

    private final Logger LOGGER = LoggerFactory.getLogger(HazelcastResponseService.class);

    private IHazelcastClientFactory hazelcastClientFactory;
    private String hazelcastTopicName;
    private boolean _listening = false;

    protected String getHazelcastTopicName() {
        return hazelcastTopicName;
    }

    public void setHazelcastTopicName(String hazelcastTopicName) {
        this.hazelcastTopicName = hazelcastTopicName;
    }

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public void init() {
        Runnable command = new MessageListenerBootstrap(this);
        // DaspGlobal.getScheduler().scheduleAtFixedRate(command, 10000, 5000,
        // TimeUnit.MILLISECONDS);
        DaspGlobal.getScheduler().schedule(command, 10000, TimeUnit.MILLISECONDS);
    }

    public void destroy() {
        hazelcastClientFactory.unsubscribeFromTopic(hazelcastTopicName, this);
    }

    private class MessageListenerBootstrap implements Runnable {
        private MessageListener<HettyProtoBuf.Response> messageListener;

        public MessageListenerBootstrap(MessageListener<HettyProtoBuf.Response> messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public void run() {
            if (!_listening) {
                try {
                    hazelcastClientFactory.subcribeToTopic(hazelcastTopicName, messageListener);
                    _listening = true;
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if (!_listening) {
                DaspGlobal.getScheduler().schedule(this, 5000, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onMessage(Message<HettyProtoBuf.Response> message) {
        writeResponse(message.getMessageObject());
    }
}
