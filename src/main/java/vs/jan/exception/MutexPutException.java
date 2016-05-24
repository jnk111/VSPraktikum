package vs.jan.exception;

public class MutexPutException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8865118531679987057L;

	public MutexPutException() {
		super();
	}

	public MutexPutException(String msg) {
		super(msg);
	}

}
