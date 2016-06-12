package vs.jan.services.decks;

import vs.jan.model.Convertable;

public enum CommCard implements Convertable<JSONCard> {

	
	GET_MONEY_FROM_BANK("get 2000$ from Bank",
			"get 2000$ from Bank"), GET_MONEY_FROM_ALL_PLAYERS("get 1000$ from every player", "get 1000$ from every player");

	private String name;
	private String text;

	public final static int BANK_MONEY = 2000;
	public final static int PLAYER_MONEY = 1000;

	private CommCard(String name, String text) {
		this.name = name;
		this.text = text;
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
