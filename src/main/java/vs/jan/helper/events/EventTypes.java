package vs.jan.helper.events;

public enum EventTypes {

	ROLL_DICE("DiceRoll"), MOVE_PAWN("move"), VISIT_PLACE("visit"), PAY_RENT("payrent"), CANNOT_PAY_RENT(
			"cannotpayrent"), BUY_PLACE("buyplace"), CANNOT_BUY_PLACE("cannotbuyplace"), TAKE_HYPO(
					"takehypothecary"), DELETE_HYPO("deletehypothecary"), CANNOT_DELETE_HYPO(
							"cannotdeletehypothecary"), MOVED_TO_JAIL("movedtojail"), TRADE_PLACE("tradeplace"), CANNOT_TRADE_PLACE(
									"cannottradeplace"), GOT_MONEY_ALL_PLAYERS("gotmoneyallplayers"), GOT_MONEY_FROM_BANK(
											"got money from bank"), MOVED_OVER_GO("movedovergo"), CANNOT_PAY_MONEY_COMMUNITY(
													"cannotpaymoney"), MUTEX_CHANGE("mutexchanged"), GAME_STARTED("gamestarted"), CANNOT_PAY_TAX(
															"cannotpaytax"), PAYED_TAX("payedtax"), TRADE_REQ("tradereq"), BUY_HOUSE("buyedhouse");

	private String type;

	private EventTypes(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
