package vs.jan.model.exception;

public class TransactionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2450349589672015645L;

	public TransactionFailedException() {
		super();
	}

	public TransactionFailedException(String msg) {
		super(msg);
	}

}
