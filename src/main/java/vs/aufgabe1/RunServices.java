package vs.aufgabe1;

import vs.aufgabe1.diceservice.RollDiceService;
import vs.aufgabe1.userservice.UserService;

public class RunServices {

	/**
	 * Startet die beiden Services
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Je nach dem welcher Service aktiv sein soll, Zeile kommentieren und als Runnable
		// JAR exportieren
		new UserService();		 
		new RollDiceService();
		// ... weitere Services
	}


}
