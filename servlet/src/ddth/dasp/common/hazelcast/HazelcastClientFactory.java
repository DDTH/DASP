package ddth.dasp.common.hazelcast;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.security.UsernamePasswordCredentials;

import ddth.dasp.common.DaspGlobal;

public class HazelcastClientFactory implements IHazelcastClientFactory {
    private String hazelcastUsername, hazelcastPassword;
    private List<String> hazelcastServers;
    private ClientConfig clientConfig;
    private HazelcastClient _hazelcastClient;
    private AtomicLong counter = new AtomicLong(0);

    // private long lastAccessTimestamp = System.currentTimeMillis();

    public void setHazelcastUsername(String hazelcastUsername) {
        this.hazelcastUsername = hazelcastUsername;
    }

    public void setHazelcastPassword(String hazelcastPassword) {
        this.hazelcastPassword = hazelcastPassword;
    }

    public void setHazelcastServers(List<String> hazelcastServers) {
        this.hazelcastServers = hazelcastServers;
    }

    public void init() {
        clientConfig = new ClientConfig();
        // ClientConfig clientConfig = new ClientConfig();
        // clientConfig.setConnectionTimeout(10000);
        // clientConfig.setReconnectionAttemptLimit(10);
        // clientConfig.setInitialConnectionAttemptLimit(10);
        // clientConfig.setReConnectionTimeOut(10000);
        if (!StringUtils.isBlank(hazelcastUsername)) {
            clientConfig.setCredentials(new UsernamePasswordCredentials(hazelcastUsername,
                    hazelcastPassword));
        }
        for (String hazelcastServer : hazelcastServers) {
            clientConfig.addAddress(hazelcastServer);
        }
        getHazelcastClient();
        ScheduledExecutorService ses = DaspGlobal.getScheduler();
        Runnable command = new HazelcastClientCheck();
        ses.scheduleWithFixedDelay(command, 10, 10, TimeUnit.SECONDS);
    }

    private class HazelcastClientCheck implements Runnable {
        @Override
        public void run() {
            try {
                HazelcastClient client = getHazelcastClient();
                client.getMap("");
            } finally {
                returnHazelcastClient();
            }
        }
    }

    public void destroy() {
        dispostHazelcastClient();
    }

    synchronized public HazelcastClient getHazelcastClient() {
        if (_hazelcastClient != null && !_hazelcastClient.isActive()) {
            dispostHazelcastClient();
        }
        if (_hazelcastClient == null) {
            _hazelcastClient = HazelcastClient.newHazelcastClient(clientConfig);
        }
        counter.incrementAndGet();
        // lastAccessTimestamp = System.currentTimeMillis();
        return _hazelcastClient;
    }

    synchronized public void returnHazelcastClient() {
        long value = counter.decrementAndGet();
        if (value < 0) {
            counter.set(0);
            throw new IllegalStateException("No Hazelcast client is currently allocated!");
        }
        if (value == 0) {
            dispostHazelcastClient();
        }
    }

    synchronized public void dispostHazelcastClient() {
        if (_hazelcastClient != null) {
            try {
                _hazelcastClient.shutdown();
            } finally {
                _hazelcastClient = null;
            }
        }
    }
}
