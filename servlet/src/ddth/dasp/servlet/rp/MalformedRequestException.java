package ddth.dasp.servlet.rp;

/**
 * This exception indicates that the request is malformed.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class MalformedRequestException extends Exception {

    /**
     * Auto-generated serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public MalformedRequestException() {
    }

    public MalformedRequestException(String errorMsg) {
        super(errorMsg);
    }

    public MalformedRequestException(String errorMsg, Throwable t) {
        super(errorMsg, t);
    }
}
