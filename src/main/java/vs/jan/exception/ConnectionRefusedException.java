package vs.jan.exception;

public class ConnectionRefusedException extends IllegalStateException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5194812463984008588L;
	
	public ConnectionRefusedException(){
		super();
	}
	
	public ConnectionRefusedException(String msg){
		super(msg);
	}

	
}
