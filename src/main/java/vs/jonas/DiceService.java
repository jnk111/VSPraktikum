package vs.jonas;

import static spark.Spark.get;

import java.util.Map;

import com.google.gson.Gson;

import vs.jan.Service;
import vs.jan.ServiceNames;

public class DiceService {

	public final String SLASH_DICE = "/dice";
	private Gson gson;
	private Map<String, Service> neededServices;
	
	
	public DiceService(Map<String, Service> neededServices){
		gson = new Gson();
		this.setNeededServices(neededServices);
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
			String eventServiceUri = neededServices.get(ServiceNames.EVENT).getUri(); 
			String code = client.sendCreateEventRequest(event, eventServiceUri);
			System.out.println("EventManager ReturnCode: " + code);
			return Dice.create(player, game);
		}, gson::toJson);
	}

	public Map<String, Service> getNeededServices() {
		return neededServices;
	}

	public void setNeededServices(Map<String, Service> neededServices) {
		this.neededServices = neededServices;
	}

}
