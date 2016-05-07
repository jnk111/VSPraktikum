package vs.aufgabe2a.boardsservice.models.json;

import java.util.ArrayList;
import java.util.List;

import vs.aufgabe1.diceservice.Dice;

public class JSONThrowsList {
	
	private List<Dice> rolls;	// throws identifier not allowed -> Keyword
	
	public JSONThrowsList(){
		
		setRolls(new ArrayList<>());
	}

	public List<Dice> getRolls() {
		return rolls;
	}

	public void setRolls(List<Dice> rolls) {
		this.rolls = rolls;
	}
	
	
	public void addThrow(Dice dice){
		rolls.add(dice);
	}
	
	
	

}
