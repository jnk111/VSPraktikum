package vs.gerriet.exception;

/**
 * <p>
 * Exception class for bank API errors.
 * </p>
 * <p>
 * This extends a Runtime Exception so API classes can safely throw
 * ApiExceptions without being limited when implementing interfaces.
 * </p>
 *
 * @author Gerriet Hinrichs {@literal<gerriet.hinrichs@web.de>}
 */
public class ApiException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3968871304912889849L;

    /**
     * Creates a new exception.
     *
     * @param message
     *            Short error description.
     */
    public ApiException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message
     *            Short error description.
     * @param cause
     *            Reason for the API error.
     */
    public ApiException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause
     *            Reason for the API error.
     */
    public ApiException(final Throwable cause) {
        super(cause);
    }
}
