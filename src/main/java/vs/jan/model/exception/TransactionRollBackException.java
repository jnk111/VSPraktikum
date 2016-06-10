package vs.jan.model.exception;

public class TransactionRollBackException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5720824611320686593L;
	
	public TransactionRollBackException() {
		super();
	}
	
	public TransactionRollBackException(String msg) {
		super(msg);
	}

}
