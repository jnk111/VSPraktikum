package vs.jan.validator.brokerservice;

import vs.jan.exception.InvalidInputException;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.model.Validable;

public class BrokerValidator {

	public void checkGameUriIsValid(JSONGameURI game) {
		
		if(game == null || !game.isValid()){
			throw new InvalidInputException();
		}
	}
	
	public void checkJsonIsValid(Validable json){
		if(!json.isValid()){
			throw new InvalidInputException();
		}
	}

	public void checkIdIsNotNull(String gameid) {
		if(gameid == null){
			throw new InvalidInputException();
		}
	}	
}
