package vs.aufgabe1.diceservice;

public class Dice {
	
	private int number;
	
	public void rollDice(){
		this.number = (int) ((Math.random() * 6) + 1);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int roll) {
		this.number = roll;
	}

}
