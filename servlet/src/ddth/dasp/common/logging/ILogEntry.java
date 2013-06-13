package ddth.dasp.common.logging;

public interface ILogEntry {
    public Object getField(String name);

    public void setField(String name, Object value);
}
