package ddth.dasp.framework.scheduletask;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import ddth.dasp.framework.scheduletask.ITask.Scheduling;

public class TaskSchedulingInfo implements Serializable {
    private static final long serialVersionUID = "$Id: $".hashCode();

    private ITask.Scheduling scheduling;
    private Object params;
    private ITask task;
    private long initialDelay, fixedRateDelay;
    private TimeUnit initialTimeUnit, fixedRateTimeUnit;

    public TaskSchedulingInfo() {
    }

    public TaskSchedulingInfo(ITask task, Scheduling scheduling, Object params, long initialDelay,
            TimeUnit initialTimeUnit, long fixedRateDelay, TimeUnit fixedRateTimeUnit) {
        setTask(task);
        setScheduling(scheduling);
        setParams(params);
        setInitialDelay(initialDelay);
        setInitialTimeUnit(initialTimeUnit);
        setFixedRateDelay(fixedRateDelay);
        setFixedRateTimeUnit(fixedRateTimeUnit);
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public TaskSchedulingInfo setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public long getFixedRateDelay() {
        return fixedRateDelay;
    }

    public TaskSchedulingInfo setFixedRateDelay(long fixedRateDelay) {
        this.fixedRateDelay = fixedRateDelay;
        return this;
    }

    public TimeUnit getInitialTimeUnit() {
        return initialTimeUnit;
    }

    public TaskSchedulingInfo setInitialTimeUnit(TimeUnit initialTimeUnit) {
        this.initialTimeUnit = initialTimeUnit;
        return this;
    }

    public TimeUnit getFixedRateTimeUnit() {
        return fixedRateTimeUnit;
    }

    public TaskSchedulingInfo setFixedRateTimeUnit(TimeUnit fixedRateTimeUnit) {
        this.fixedRateTimeUnit = fixedRateTimeUnit;
        return this;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public void setScheduling(Scheduling scheduling) {
        this.scheduling = scheduling;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public ITask getTask() {
        return task;
    }

    public void setTask(ITask task) {
        this.task = task;
    }
}
