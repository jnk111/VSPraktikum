package vs.jan.exception;

public class DiceRollFailedException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7031841553672477652L;
	
	public DiceRollFailedException(){
		super();
	}
	
	public DiceRollFailedException(String msg){
		super(msg);
	}
}
