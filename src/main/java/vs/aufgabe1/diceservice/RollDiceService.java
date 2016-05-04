package vs.aufgabe1.diceservice;
import static spark.Spark.get;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import spark.Response;
import vs.aufgabe1.StatusCodes;
import vs.aufgabe1.userservice.User;


public class RollDiceService {
	
	public RollDiceService(){
		initGETDefault();
	}
	
	/**
	 * Default-Rolldice - Aufgabe 1.2.2A - 1.)
	 */
	private void initGETDefault() {
		
		get("/dice", "application/json", (req, res) -> {
			
			String playerUri = req.queryParams("player");
			String gameUri = req.queryParams("game");
			String response = null;
			if(playerUri == null 
					&& gameUri == null){
				response = rollDiceDefault();
			}else{
				response = rollDiceAdvanced(playerUri, gameUri, res);
			}
			return response;
		});
	}
		
	
	/**
	 * Roll-Dice mit Spieler und Gameinfo Aufgabe 1.2.2A - 2.
	 * @param playerUri
	 * @param gameUri
	 * @param res
	 * @return
	 */
	private String rollDiceAdvanced(String playerUri, 
																	String gameUri, 
																	Response res) {
		
		Gson gson = new Gson();
		Map<User, Dice> rollAction = new HashMap<>();
		String response = null;
		String userJson = null;
		// String gameJson = null;
		
		HttpResponse<JsonNode> playerResp = null;
		// HttpResponse<JsonNode> gameResp = null;
		
		try {
			playerResp = Unirest.get(playerUri).asJson();
			// gameResp = Unirest.get(gameUri).asJson();
			
			userJson = playerResp.getBody().toString();
			// gameJson = gameResp.getBody().toString();
			
		} catch (UnirestException e) {
			e.printStackTrace();					// TODO: Andere Fehlerbehandlung
		}

		User user = new Gson().fromJson(userJson, User.class);
		// Game game = new Gson().fromJson(gameJson, Game.class);
		
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
	}

	
	/**
	 * Default-Rolldice - Aufgabe 1.2.2A - 1.)
	 */
	private String rollDiceDefault() {
		Dice dice = new Dice();
		dice.rollDice();
		return new Gson().toJson(dice);
	}
}
