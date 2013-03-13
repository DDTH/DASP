package ddth.dasp.framework.scheduletask;

import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.AtomicNumber;
import com.hazelcast.core.IMap;

import ddth.dasp.common.DaspGlobal;
import ddth.dasp.common.hazelcast.IHazelcastClientFactory;

/**
 * This task registry runs tasks locally (e.g. within the JVM), but stores task
 * status/information via a Hazelcast cluster.
 * 
 * @author ThanhNB <thanhnb@vng.com.vn>
 */
public class HazelcastLocalRunClusterStatusTaskRegistry extends AbstractTaskRegistry {

    private IHazelcastClientFactory hazelcastClientFactory;
    private String taskSchedulingMapName = HazelcastLocalRunClusterStatusTaskRegistry.class
            .getName();

    protected IHazelcastClientFactory getHazelcastClientFactory() {
        return hazelcastClientFactory;
    }

    public void setHazelcastClientFactory(IHazelcastClientFactory hazelcastClientFactory) {
        this.hazelcastClientFactory = hazelcastClientFactory;
    }

    public String getTaskSchedulingMapName() {
        return taskSchedulingMapName;
    }

    public void setTaskSchedulingMapName(String taskSchedulingMapName) {
        this.taskSchedulingMapName = taskSchedulingMapName;
    }

    /*---------- storage methods ----------*/
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean storeTaskSchedulingInfo(TaskSchedulingInfo schedulingInfo) {
        ITask task = schedulingInfo.getTask();
        String taskId = task.getId();
        HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                IMap<String, TaskSchedulingInfo> storage = hazelcastClient
                        .getMap(taskSchedulingMapName);
                if (storage != null) {
                    storage.put(taskId, schedulingInfo);
                    return true;
                }
            } catch (Exception e) {
                return false;
            } finally {
                getHazelcastClientFactory().returnHazelcastClient();
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskSchedulingInfo retrieveTaskSchedulingInfo(String taskId) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
            try {
                IMap<String, TaskSchedulingInfo> storage = hazelcastClient
                        .getMap(taskSchedulingMapName);
                return storage.get(taskId);
            } finally {
                getHazelcastClientFactory().returnHazelcastClient();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeTaskSchedulingInfo(String taskId) {
        HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
        if (hazelcastClient != null) {
            try {
                IMap<String, TaskSchedulingInfo> storage = hazelcastClient
                        .getMap(taskSchedulingMapName);
                if (storage != null) {
                    storage.remove(taskId);
                    return true;
                }
            } catch (Exception e) {
                return false;
            } finally {
                getHazelcastClientFactory().returnHazelcastClient();
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITask.Status getTaskStatus(String taskId) {
        HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
        try {
            AtomicNumber concurrentLevel = hazelcastClient.getAtomicNumber(taskId);
            return concurrentLevel == null ? null : (concurrentLevel.get() == 0 ? ITask.Status.IDLE
                    : ITask.Status.RUNNING);
        } finally {
            getHazelcastClientFactory().returnHazelcastClient();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preRunning(String taskId) {
        HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
        try {
            AtomicNumber concurrentLevel = hazelcastClient.getAtomicNumber(taskId);
            concurrentLevel.incrementAndGet();
        } finally {
            getHazelcastClientFactory().returnHazelcastClient();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postRunning(String taskId) {
        HazelcastClient hazelcastClient = getHazelcastClientFactory().getHazelcastClient();
        try {
            AtomicNumber concurrentLevel = hazelcastClient.getAtomicNumber(taskId);
            concurrentLevel.decrementAndGet();
        } finally {
            getHazelcastClientFactory().returnHazelcastClient();
        }
    }

    /*---------- storage methods ----------*/

    /**
     * {@inheritDoc}
     */
    @Override
    protected void scheduleToRun(String taskId, boolean firstTime) {
        TaskSchedulingInfo schedulingInfo = getTaskSchedulingInfo(taskId);
        if (schedulingInfo != null) {
            long delay = firstTime ? schedulingInfo.getInitialDelay() : schedulingInfo
                    .getFixedRateDelay();
            TimeUnit timeUnit = firstTime ? schedulingInfo.getInitialTimeUnit() : schedulingInfo
                    .getFixedRateTimeUnit();
            if (!firstTime && schedulingInfo.getScheduling() == ITask.Scheduling.CONTINUOUS) {
                delay = 0;
            }
            Runnable command = new LocalTaskRunner(schedulingInfo);
            DaspGlobal.getScheduler().schedule(command, delay, timeUnit);
        }
    }

    private class LocalTaskRunner extends TaskRunner {
        public LocalTaskRunner(TaskSchedulingInfo schedulingInfo) {
            super(schedulingInfo);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object internalRunTask(ITask task, Object params) {
            return task.executeTask(params);
        }
    }
}
