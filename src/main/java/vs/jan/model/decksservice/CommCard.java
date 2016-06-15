package vs.jan.model.decksservice;

import vs.jan.json.decksservice.JSONCard;
import vs.jan.model.Convertable;

public enum CommCard implements Convertable<JSONCard> {

	GET_MONEY_FROM_BANK("get <money>$ from Bank", "get <money>$ from Bank"), GET_MONEY_FROM_ALL_PLAYERS(
			"get <money>$ from every player", "get <money>$ from every player");

	private String name;
	private String text;

	public final static int BANK_MONEY = 2000;
	public final static int PLAYER_MONEY = 1000;
	private final String PLACE_HOLDER = "<money>";

	private CommCard(String name, String text) {
		this.name = name.replace(PLACE_HOLDER, String.valueOf(BANK_MONEY));
		this.text = text.replace(PLACE_HOLDER, String.valueOf(BANK_MONEY));
	}

	@Override
	public JSONCard convert() {
		JSONCard card = new JSONCard(this.name, this.text);
		return card;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
