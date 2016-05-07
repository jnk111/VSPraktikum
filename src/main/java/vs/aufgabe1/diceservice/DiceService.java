package vs.aufgabe1.diceservice;

import static spark.Spark.get;

import com.google.gson.Gson;

import vs.aufgabe1b.RestClient;
import vs.aufgabe1b.models.Event;

public class DiceService {

	public final String SLASH_DICE = "/dice";
	private Gson gson;
	
	public DiceService(){
		gson = new Gson();
	}
	
	/**
	 * Starts the DiceService
	 */
	public void startService(){		
		
		get(SLASH_DICE,(req,res) ->{
			res.status(200);
			String player = req.queryParams("player");
			String game = req.queryParams("game");
			// Event(String game, String type, String name, String reason, String ressource, String player)
			Event event = new Event(game,"DiceRoll","DiceRoll",player+" has rolled the dice.", SLASH_DICE,player);
			RestClient client = new RestClient();
			String code = client.sendCreateEventRequest(event);
			System.out.println("EventManager ReturnCode: " + code);
			return Dice.create(player, game);
		}, gson::toJson);
	}
}
