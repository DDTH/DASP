package ddth.dasp.framework.scheduletask;

public interface ITask {

    public static enum Status {
        RUNNING, // task is running
        IDLE, // task is idle
    };

    public static enum Scheduling {
        RUNONCE, // task runs only once
        CONTINUOUS, // task runs continuously (ignore fixed rate delay)
        REPEATED, // task runs repeatedly at a fixed rate
    };

    /**
     * Gets task's unique id.
     * 
     * @return
     */
    public String getId();

    /**
     * Executes task.
     * 
     * @param params
     * @return
     */
    public Object executeTask(Object params);

    /**
     * Does this task allow concurrent runs?
     * 
     * @return
     */
    public boolean isAllowConcurrent();
}
