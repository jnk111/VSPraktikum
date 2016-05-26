package vs.jan.model.exception;

public enum Error {
	JSON_GAME_URI("game uri is not valid"), JSON_PAWN("pawn input is not valid"), BOARD_INIT(
			"board is not initiliazed"), GAME_ID("game id must not be null"), ROLL_VALUE(
					"roll value is smaller than one"), JSON_PLACE("place input is not valid"), PLACE_ID(
							"place id is null"), PAWN_ID("pawn id is null"), JSON_BOARD("board is not valid"), MUTEX(
									"player does not hold the mutex"), PLACE_ID_NUM("place id must be a number"), BOARD_NOT_FOUND(
											"board not found"), PAWN_NOT_FOUND("pawn not found"), PLACE_NOT_FOUND(
													"place not found"), ROLL_FAIL("dice roll has failed, there was no valid response");

	private String msg;

	private Error(String msg) {
		this.msg = msg;
	}

	public String getMsg() {

		return this.msg;
	}

}