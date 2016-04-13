package vs.aufgabe1.diceservice;
import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import vs.aufgabe1.StatusCodes;
import vs.aufgabe1.userservice.User;


public class RollDiceService {
	
	public RollDiceService(){
		initGETWithPlayerInfo();
		initGETDefault();
	}
	
	/**
	 * Default-Rolldice - Aufgabe 1.2.2A - 1.)
	 */
	private void initGETDefault() {
		
		get("/dice", "application/json", (req, res) -> {
			
			Dice dice = new Dice();
			dice.rollDice();
			return new Gson().toJson(dice);
		});
	}
		

	/**
	 * Default-Rolldice - Aufgabe 1.2.2A - 2.)
	 * FUNKTIONIERT NICHT! Wir muessen rauskriegen wie man einen REST-Aufruf in Spark macht.
	 */
	private void initGETWithPlayerInfo() {
		
		get("/dice2", "application/json", (req, res) -> {
			
			Gson gson = new Gson();
			String response = null;
			Map<User, Dice> rollAction = new HashMap<>();
			String playerUri = req.queryParams("name");
			// String gameUri = req.queryParams("game");  --> Spaeter
			System.out.println(playerUri);
			
			if(playerUri != null){				// Funktioniert nicht
				res.redirect(playerUri);
			}
			
			User user = gson.fromJson(res.body(), User.class);
			
			if(user != null
					&& user.isValid()){
				Dice dice = new Dice();
				dice.rollDice();
				rollAction.put(user, dice);
				res.status(StatusCodes.SUCCESS);
				response = gson.toJson(rollAction);
			}else{
				res.status(StatusCodes.BAD_REQ);
				response = gson.toJson(StatusCodes.BAD_REQ + ": User not found");
			}
			return response;
		});
		
	}
}
