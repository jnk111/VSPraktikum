package vs.jan.exception;

public class ResponseCodeException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5551395402010296118L;
	
	public ResponseCodeException(){
		super();
	}
	
	public ResponseCodeException(String msg){
		super(msg);
	}

}
