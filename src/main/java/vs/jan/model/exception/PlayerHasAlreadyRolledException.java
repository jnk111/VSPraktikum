package vs.jan.model.exception;

public class PlayerHasAlreadyRolledException extends IllegalStateException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3328716344770280063L;
	
	public PlayerHasAlreadyRolledException(){
		super();
	}
	
	public PlayerHasAlreadyRolledException(String msg) {
		super(msg);
	}

}
