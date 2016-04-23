package vs.aufgabe2b.exceptions.bank;

/**
 * Exception class for account access errors.
 * 
 * @author Gerriet Hinrichs <gerriet.hinrichs@web.de>
 */
public class AccountAccessException extends Exception {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5384250035735724532L;

	/**
	 * Creates a new exception.
	 * 
	 * @param message
	 *            Short error description.
	 * @param cause
	 *            Reason for the access error.
	 */
	public AccountAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new exception.
	 * 
	 * @param message
	 *            Short error description.
	 */
	public AccountAccessException(String message) {
		super(message);
	}

	/**
	 * Creates a new exception.
	 * 
	 * @param cause
	 *            Reason for the access error.
	 */
	public AccountAccessException(Throwable cause) {
		super(cause);
	}
}
