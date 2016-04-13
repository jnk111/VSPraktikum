package vs.aufgabe1;

import vs.aufgabe1.diceservice.RollDiceService;
import vs.aufgabe1.userservice.UserService;

public class RunServices {

	/**
	 * Startet die beiden Services
	 * @param args
	 */
	public static void main(String[] args) {
		
		new UserService();
		new RollDiceService();

	}

}
