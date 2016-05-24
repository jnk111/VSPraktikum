package vs.jan.validator.boardservice;

import vs.jan.exception.BoardNotInitiliazedException;
import vs.jan.exception.ExcMessageHandler;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.json.JSONBoard;
import vs.jan.json.JSONGameURI;
import vs.jan.json.JSONPawn;
import vs.jan.json.JSONPlace;
import vs.jan.model.Board;
import vs.jan.model.Place;
import vs.jan.model.PlaceBkp;

public class BoardServiceValidator {

	
	public void checkGameIsValid(JSONGameURI game) {
		if (!game.isValid()) {
			throw new InvalidInputException(ExcMessageHandler.getGameUriNotValidMsg());
		}
	}

	public void checkBoardIsNotNull(Board b, String gameid) {
		if (b == null) {
			throw new ResourceNotFoundException(ExcMessageHandler.getBoardNotFoundMsg(gameid));
		}
	}

	public void checkPawnInputIsValid(JSONPawn pawn) {
		if (!pawn.isValid()) {
			throw new InvalidInputException(ExcMessageHandler.getPawnInputNotValidMsg(pawn.getId()));
		}
	}

	public void checkBoardHasFields(Board b, String gameid) {
		if (!b.hasFields()) {
			throw new BoardNotInitiliazedException(ExcMessageHandler.getBoardNotInitializedMsg(gameid));
		}
	}

	public void checkGameIdIsNotNull(String gameid) {
		if (gameid == null) {
			throw new InvalidInputException(ExcMessageHandler.getGameUriNotValidMsg());
		}
	}

	public void checkPawnIsNotNull(String pawnid, String gameid) {
		if (pawnid == null) {
			throw new ResourceNotFoundException(ExcMessageHandler.getPawnNotFoundMsg(pawnid, gameid));
		}
	}

	public void checkRollValueIsValid(int rollValue) {
		if (rollValue <= 0) {
			throw new InvalidInputException(ExcMessageHandler.getRollValueNotValidMsg(rollValue));
		}
	}

	public void checkPlaceIsNotNull(Place p, String pathinfo, String gameid) {
		if (p == null) {
			throw new ResourceNotFoundException(ExcMessageHandler.getPlaceNotFoundMsg(pathinfo, gameid));
		}
	}

	public void checkPlaceIsValid(JSONPlace place) {
		if (!place.isValid()) {
			throw new InvalidInputException("Place is Not Valid: Placeuri and/or Brokeruri is null");
		}
	}

	public void checkPawnIsValid(JSONPawn p) {
		if (!p.isValid()) {
			throw new InvalidInputException(ExcMessageHandler.getPawnInputNotValidMsg(p.getId()));
		}
	}
	
	public void checkPlaceIdIsNotNull(PlaceBkp p, String gameid) {
		if(p == null){
			throw new ResourceNotFoundException(ExcMessageHandler.getPlaceIdIsNullMsg(gameid));
		}
	}

	public void checkPlaceIdIsNotNull(String placeid, String gameid) {
		if(placeid == null){
			throw new InvalidInputException(ExcMessageHandler.getPlaceIdIsNullMsg(gameid));
		}
	}
	
	public void checkPawnIdIsNotNull(String pawnid) {
		if(pawnid == null){
			throw new ResourceNotFoundException(ExcMessageHandler.getPawnIdIsNullMsg());
		}
	}
	
	public void checkBoardIsValid(String gameid, JSONBoard board) {

		if (!board.isValid()) {
			throw new InvalidInputException(ExcMessageHandler.getBoardInputNotValidMsg(gameid));
		}
	}

}
