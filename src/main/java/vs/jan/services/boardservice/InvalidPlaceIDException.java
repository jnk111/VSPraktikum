package vs.jan.services.boardservice;

public class InvalidPlaceIDException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7384410943829891892L;

	public InvalidPlaceIDException() {
		super();
	}

	public InvalidPlaceIDException(String msg) {

		super(msg);
	}

}
