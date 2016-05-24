package vs.jan.exception;

public class ExcMessageHandler {

	public static String getBoardNotFoundMsg(String gameid) {
		return "Board with id: " + gameid + " not found!";
	}

	public static String getGameUriNotValidMsg() {
		return "No GameUri is not valid!";
	}

	public static String getPawnInputNotValidMsg(String id) {
		return "Pawn: " + id + " has no place or playeruri!";
	}

	public static String getBoardNotInitializedMsg(String gameid) {
		return "Board: " + gameid + " has no initizliazed Fields";
	}

	public static String getRollValueNotValidMsg(int rollValue) {
		return "Value of the dice roll is not valid: " + rollValue + " <= 0";
	}

	public static String getPawnNotFoundMsg(String pawnid, String gameid) {

		return "Pawn with pawnid: " + pawnid + " has not been found on board with gameid: " + gameid;
	}

	public static String getPlaceNotFoundMsg(String placeuri, String gameid) {
		return "Place with the uri: " + placeuri + " has not been found on the board with gameid: " + gameid;
	}

	public static String getPlaceIdIsNullMsg(String gameid) {

		return "The given place id is null, Game: " + gameid;
	}

	public static String getPawnIdIsNullMsg() {

		return "the given pawnid was null";
	}

	public static String getBoardInputNotValidMsg(String gameid) {
		return "The Given Board Information for the board: " + gameid + " is not valid or incomplete!";
	}

}
