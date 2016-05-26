package vs.jonas.client.json;

import java.util.List;

import vs.jonas.services.model.Dice;

public class DiceRolls {
	
	List<Dice> rolls;

	public DiceRolls(List<Dice> rolls) {
		super();
		this.rolls = rolls;
	}

	public List<Dice> getRolls() {
		return rolls;
	}
	
	
}
