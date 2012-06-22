package ddth.dasp.common.rp;

/**
 * This exception indicates that the request parsing process has been
 * interrupted.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class RequestParsingInteruptedException extends Exception {

    /**
     * Auto-generated serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public RequestParsingInteruptedException() {
    }

    public RequestParsingInteruptedException(String errorMsg) {
        super(errorMsg);
    }

    public RequestParsingInteruptedException(String errorMsg, Throwable t) {
        super(errorMsg, t);
    }
}
