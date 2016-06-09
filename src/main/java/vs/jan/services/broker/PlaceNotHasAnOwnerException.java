package vs.jan.services.broker;

import vs.jan.exception.ResourceNotFoundException;

public class PlaceNotHasAnOwnerException extends ResourceNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1545129642903425321L;
	
	public PlaceNotHasAnOwnerException() {
		super();
	}
	
	public PlaceNotHasAnOwnerException(String msg) {
		super(msg);
	}

}
