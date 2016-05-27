package vs.jan.validator;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import vs.jan.exception.BoardNotInitiliazedException;
import vs.jan.exception.DiceRollFailedException;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.TurnMutexNotFreeException;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Validable;
import vs.jan.model.boardservice.Board;
import vs.jan.model.boardservice.Player;
import vs.jan.model.exception.Error;
import vs.jan.tools.HttpService;
import vs.jonas.services.model.Dice;

public class Validator {

	private final Gson GSON = new Gson();

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


	public void checkBoardHasFields(Board b) {
		if (!b.hasFields()) {
			throw new BoardNotInitiliazedException(Error.BOARD_INIT.getMsg());
		}
	}

	public void checkRollValueIsValid(Dice roll) {
		
		checkDiceNotNull(roll);
		if (roll.getNumber() <= 0) {
			throw new InvalidInputException(Error.ROLL_VALUE.getMsg());
		}
	}

	public void checkDiceNotNull(Dice roll) {
		
		if(roll == null){
			throw new DiceRollFailedException(Error.ROLL_FAIL.getMsg());
		}
		
	}


	/**
	 * TODO: local
	 * @param gameid
	 * @param pawnid
	 */
	public void checkPlayerHasMutex(String gameid, String pawnid, String gameServiceUri) {

		String player = gameServiceUri + "/" + gameid + "/player/turn";
		String json = HttpService.get(player, HttpURLConnection.HTTP_OK);
		Player current = GSON.fromJson(json, Player.class);
		
		if (!current.getPawn().contains(pawnid)) {
			throw new TurnMutexNotFreeException(Error.MUTEX.getMsg());
		}
	}


	public Gson getGSON() {
		return GSON;
	}

}
