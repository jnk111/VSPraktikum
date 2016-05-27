package vs.jan.model.exception;

public class ResponseCodeException extends Exception {

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
