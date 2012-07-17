package ddth.dasp.common.logging;

/**
 * Application profiling logger.
 * 
 * Usage:
 * 
 * <pre>
 * ProfileLogger.push(&quot;marker_name_1&quot;);
 * // do some work
 * ProfileLogger.pop();
 * 
 * ProfileLogger.push(&quot;marker_name_2&quot;);
 * // do some other work
 * ProfileLogger.push(&quot;marker_name_child_1&quot;);
 * // do some child work
 * ProfileLogger.pop();
 * ProfileLogger.pop();
 * 
 * ProfileLogEntry logEntry = ProfileLogger.get();
 * // do something with logEntry
 * </pre>
 * 
 * @author ThanhNB
 */
public class ProfileLogger {

    protected static ThreadLocal<ProfileLogEntry> logEntry = new ThreadLocal<ProfileLogEntry>() {
        /**
         * {@inheritDoc}
         */
        @Override
        protected ProfileLogEntry initialValue() {
            return new ProfileLogEntry();
        }
    };

    /**
     * Gets the currently bound {@link ProfileLogEntry} object. Creates one if
     * not exists.
     * 
     * @return ProfileLogEntry
     */
    public static ProfileLogEntry get() {
        ProfileLogEntry entry = logEntry.get();
        return entry;
    }

    /**
     * Removes the currently bound {@link ProfileLogEntry} object.
     */
    public static void remove() {
        logEntry.remove();
    }

    /**
     * Pushes a profiling to the stack.
     * 
     * @param name
     *            String
     * @return ProfileLogEntry the currently bound {@link ProfileLogEntry}
     *         object.
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
