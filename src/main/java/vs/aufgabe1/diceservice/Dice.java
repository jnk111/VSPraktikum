package vs.aufgabe1.diceservice;

public class Dice {
	
	private int number;
	
	public void rollDice(){
		this.number = (int) ((Math.random() * 6) + 1);
	}

	public int getRoll() {
		return number;
	}

	public void setRoll(int roll) {
		this.number = roll;
	}

}
