package vs.jan.exception;

public class TurnMutexNotFreeException extends IllegalStateException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5536166636936510375L;

	public TurnMutexNotFreeException(){
		super();
	}
	
	public TurnMutexNotFreeException(String msg){
		super(msg);
	}

}
