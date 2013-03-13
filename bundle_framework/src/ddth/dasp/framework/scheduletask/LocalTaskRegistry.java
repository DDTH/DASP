package ddth.dasp.framework.scheduletask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AtomicLongMap;

import ddth.dasp.common.DaspGlobal;

/**
 * This task registry manages and runs tasks locally (e.g. within the JVM).
 * 
 * @author ThanhNB <thanhnb@vng.com.vn>
 */
public class LocalTaskRegistry extends AbstractTaskRegistry {

    private final ConcurrentMap<String, TaskSchedulingInfo> taskRegistry = new ConcurrentHashMap<String, TaskSchedulingInfo>();
    private final AtomicLongMap<String> taskStatus = AtomicLongMap.create();

    // public void init() {
    // super.init();
    // }
    //
    // public void destroy() {
    // super.destroy();
    // }

    /*---------- storage methods ----------*/
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean storeTaskSchedulingInfo(TaskSchedulingInfo schedulingInfo) {
        ITask task = schedulingInfo.getTask();
        String taskId = task.getId();
        taskRegistry.put(taskId, schedulingInfo);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskSchedulingInfo retrieveTaskSchedulingInfo(String taskId) {
        return taskRegistry.get(taskId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeTaskSchedulingInfo(String taskId) {
        taskRegistry.remove(taskId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITask.Status getTaskStatus(String taskId) {
        long concurrentLevel = taskStatus.containsKey(taskId) ? taskStatus.get(taskId) : -1;
        return concurrentLevel < 0 ? null : (concurrentLevel == 0 ? ITask.Status.IDLE
                : ITask.Status.RUNNING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void preRunning(String taskId) {
        taskStatus.incrementAndGet(taskId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postRunning(String taskId) {
        taskStatus.decrementAndGet(taskId);
    }

    /*---------- storage methods ----------*/

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // protected void scheduleToRun(final TaskSchedulingInfo schedulingInfo,
    // boolean firstTime) {
    // long delay = firstTime ? schedulingInfo.getInitialDelay() :
    // schedulingInfo
    // .getFixedRateDelay();
    // TimeUnit timeUnit = firstTime ? schedulingInfo.getInitialTimeUnit() :
    // schedulingInfo
    // .getFixedRateTimeUnit();
    // if (!firstTime && schedulingInfo.getScheduling() ==
    // ITask.Scheduling.CONTINUOUS) {
    // delay = 0;
    // }
    // Runnable command = new TaskRunner(schedulingInfo);
    // DaspGlobal.getScheduler().schedule(command, delay, timeUnit);
    // }

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
