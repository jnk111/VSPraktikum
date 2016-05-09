package vs.aufgabe2b.exceptions.bank;

/**
 * Class for transaction exceptions.
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class TransactionException extends Exception {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -6787078477242481400L;

    /**
     * Creates a new exception.
     *
     * @param message
     *            Short error description.
     */
    public TransactionException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message
     *            Short error description.
     * @param cause
     *            Reason for the access error.
     */
    public TransactionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause
     *            Reason for the access error.
     */
    public TransactionException(final Throwable cause) {
        super(cause);
    }
}
