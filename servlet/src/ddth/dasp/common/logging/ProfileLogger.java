package ddth.dasp.common.logging;

/**
 * This class represents a profiling log entry.
 * 
 * Log information:
 * <ul>
 * <li>Log's id: unique of the log entry.
 * <li>Request id: id of the associated request.
 * <li>Node's id: id of the node (app server) where the log entry originates.
 * <li>Client's id: id of the client where the request originates.
 * <li>Enduser's id: id of the enduser.
 * <li>Timestamp: timestamp when the log occurs.
 * </ul>
 * 
 * @author ThanhNB
 */
public class ProfileLogger {

    protected static ThreadLocal<ProfileLogEntry> logEntry = new ThreadLocal<ProfileLogEntry>();

    /**
     * Gets the currently bound {@link ProfileLogEntry} object. Creates one if not exists.
     * 
     * @return ProfileLogEntry
     */
    public static ProfileLogEntry get() {
        synchronized (logEntry) {
            ProfileLogEntry entry = logEntry.get();
            if (entry == null) {
                entry = new ProfileLogEntry();
                logEntry.set(entry);
            }
            return entry;
        }
    }

    /**
     * Removes the currently bound {@link ProfileLogEntry} object.
     */
    public static void remove() {
        synchronized (logEntry) {
            logEntry.remove();
        }
    }

    /**
     * Pushes a profiling to the stack.
     * 
     * @param name
     *            String
     * @return ProfileLogEntry the currently bound {@link ProfileLogEntry} object.
     */
    public static ProfileLogEntry push(String name) {
        ProfileLogEntry entry = get();
        entry.push(name);
        return entry;
    }

    /**
     * Pops the last profiling from the stack.
     * 
     * @return ProfileLogEntry
     */
    public static ProfileLogEntry pop() {
        ProfileLogEntry entry = get();
        entry.pop();
        return entry;
    }
}
