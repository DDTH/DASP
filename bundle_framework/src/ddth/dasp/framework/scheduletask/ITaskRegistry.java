package ddth.dasp.framework.scheduletask;

public interface ITaskRegistry {
    
    public final static String GLOBAL_KEY = "ALL_TASK_REGISTRIES";
    
    /**
     * Schedules a task, override old scheduling if exists.
     * 
     * @param schedulingInfo
     * @return
     */
    public boolean scheduleTask(TaskSchedulingInfo schedulingInfo);

    /**
     * Unschedules a scheduled task.
     * 
     * @param taskId
     * @return
     */
    public boolean unscheduleTask(String taskId);

    /**
     * Unschedules a scheduled task.
     * 
     * @param task
     * @return
     */
    public boolean unscheduleTask(ITask task);

    /**
     * Gets an existing task scheduling information.
     * 
     * @param taskId
     * @return
     */
    public TaskSchedulingInfo getTaskSchedulingInfo(String taskId);

    /**
     * Gets an existing task scheduling information.
     * 
     * @param task
     * @return
     */
    public TaskSchedulingInfo getTaskSchedulingInfo(ITask task);

    /**
     * Checks if a task is running.
     * 
     * @param task
     * @return
     */
    public boolean isRunning(ITask task);

    /**
     * Checks if a task is running.
     * 
     * @param taskId
     * @return
     */
    public boolean isRunning(String taskId);
}
