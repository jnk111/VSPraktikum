package vs.jonas.exceptions;

public class PlayerDoesNotHaveTheMutexException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlayerDoesNotHaveTheMutexException(String errorMsg) {
		super(errorMsg);
	}

}
