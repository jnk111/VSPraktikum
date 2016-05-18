package vs.jan.exception;

public class BoardNotInitiliazedException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4620890772219968636L;
	
	public BoardNotInitiliazedException(){
		super();
	}
	
	public BoardNotInitiliazedException(String msg){
		super(msg);
	}

}
