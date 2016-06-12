package vs.jan.services.decks;

import vs.jan.model.Convertable;

public enum ChanceCard implements Convertable<JSONCard> {
	GO_TO_JAIL("go to jail", "go to jail"), MOVE_TO_GO("move to go",
			"move to go and receive money"), MOVE_3_TIMES("move 3 fields forward", "move 3 fields forward");

	private String name;
	private String text;

	private ChanceCard(String name, String text) {
		this.name = name;
		this.text = text;
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

	@Override
	public JSONCard convert() {

		JSONCard card = new JSONCard(this.name, this.text);
		return card;
	}

}
