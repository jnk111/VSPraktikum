package vs.jan.validator;

import com.google.gson.Gson;

import vs.jan.exception.InvalidInputException;
import vs.jan.model.Validable;

public abstract class Validator {

	protected final Gson GSON = new Gson();

	public void checkJsonIsValid(Validable json, String msg) {
		if (!json.isValid()) {
			throw new InvalidInputException(msg);
		}
	}
	
	public void checkIdIsNotNull(String placeid, String msg) {
		if (placeid == null) {
			throw new InvalidInputException(msg);
		}
	}

	public Gson getGSON() {
		return GSON;
	}

	public void checkPlayerUriIsValid(String playeruri, String msg) {
		
		if(playeruri == null){
			throw new InvalidInputException(msg);
		}
	}

}
