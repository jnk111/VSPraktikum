package vs.jan.exception;

public class ResourceNotFoundException extends IllegalArgumentException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5359115195532686372L;

	public ResourceNotFoundException(){
		super();
	}
	
	public ResourceNotFoundException(String msg){
		super(msg);
	}

}
