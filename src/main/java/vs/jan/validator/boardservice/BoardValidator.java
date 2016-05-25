package vs.jan.validator.boardservice;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import vs.jan.exception.BoardNotInitiliazedException;
import vs.jan.exception.DiceRollFailedException;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.TurnMutexNotFreeException;
import vs.jan.json.JSONBoard;
import vs.jan.json.JSONGameURI;
import vs.jan.json.JSONPawn;
import vs.jan.json.JSONPlace;
import vs.jan.model.Board;
import vs.jan.model.Player;
import vs.jan.model.exception.Error;
import vs.jan.tools.HttpService;
import vs.jonas.services.model.Dice;

public class BoardValidator {

	private final Gson GSON = new Gson();

	public void checkGameIsValid(JSONGameURI game) {
		if (!game.isValid()) {
			throw new InvalidInputException(Error.JSON_GAME_URI.getMsg());
		}
	}

	public void checkPawnInputIsValid(JSONPawn pawn) {
		if (!pawn.isValid()) {
			throw new InvalidInputException(Error.JSON_PAWN.getMsg());
		}
	}

	public void checkBoardHasFields(Board b) {
		if (!b.hasFields()) {
			throw new BoardNotInitiliazedException(Error.BOARD_INIT.getMsg());
		}
	}

	public void checkGameIdIsNotNull(String gameid) {
		if (gameid == null) {
			throw new InvalidInputException(Error.GAME_ID.getMsg());
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

	public void checkPlaceIdIsNotNull(String placeid) {
		if (placeid == null) {
			throw new InvalidInputException(Error.PLACE_ID.getMsg());
		}
	}

	public void checkPawnIdIsNotNull(String pawnid) {

		if (pawnid == null) {
			throw new ResourceNotFoundException(Error.PAWN_ID.getMsg());
		}
	}

	public void checkBoardIsValid(JSONBoard board) {

		if (!board.isValid()) {
			throw new InvalidInputException(Error.JSON_BOARD.getMsg());
		}
	}

	/**
	 * TODO: local
	 * @param gameid
	 * @param pawnid
	 */
	public void checkPlayerHasMutex(String gameid, String pawnid) {

		String player = "http://localhost:4567/games/" + gameid + "/player/turn";
		String json = HttpService.get(player, HttpURLConnection.HTTP_OK);
		Player current = GSON.fromJson(json, Player.class);
		
		if (!current.getPawn().contains(pawnid)) {
			throw new TurnMutexNotFreeException(Error.MUTEX.getMsg());
		}
	}

	public void checkPlaceInputIsValid(JSONPlace place) {

		if (!place.isValid()) {
			throw new InvalidInputException(Error.JSON_PLACE.getMsg());
		}
	}

	public Gson getGSON() {
		return GSON;
	}

}
