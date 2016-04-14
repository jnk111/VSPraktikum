package vs.aufgabe1.diceservice;
import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
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
	 */
	private void initGETWithPlayerInfo() {
		
		get("/dice2", "application/json", (req, res) -> {
			
			Gson gson = new Gson();
			String response = null;
			Map<User, Dice> rollAction = new HashMap<>();
			String playerUri = req.queryParams("name");
			// String gameUri = req.queryParams("game");  --> Spaeter
			String userJson = null;
			
			if(playerUri != null){
				HttpResponse<JsonNode> o = Unirest.get(playerUri).asJson();
				userJson = o.getBody().toString();
			}
			
			User user = gson.fromJson(userJson, User.class);
			
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
