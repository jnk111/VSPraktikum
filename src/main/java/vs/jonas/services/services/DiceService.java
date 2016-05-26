package vs.jonas.services.services;

import static spark.Spark.get;

import java.util.Map;

import com.google.gson.Gson;

import vs.jan.json.JSONService;
import vs.jan.model.ServiceNames;
import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Dice;
import vs.jonas.services.model.RestClient;

public class DiceService {

	public final String SLASH_DICE = "/dice";
	private Gson gson;
	private Map<String, JSONService> neededServices;
	
	
	public DiceService(Map<String, JSONService> neededServices){
		gson = new Gson();
		this.setNeededServices(neededServices);
	}
	
	/**
	 * Starts the DiceService
	 */
	public void startService(){		
		
		get(SLASH_DICE,(req,res) ->{
			System.out.println("*** Roll Dice ***");
			System.out.println("Needed Services:"+neededServices);
			
			res.status(200);
			String playerUri = req.queryParams("player");
			String gameUri = req.queryParams("game");
			
			// Wenn die Player-Uri und die GameUri angegeben wurde, kann ein Event erstellt werden:
			if((playerUri != null && !playerUri.equals(""))&& ( gameUri != null && !gameUri.equals(""))){
				
				EventData event = new EventData(gameUri,"DiceRoll","DiceRoll",playerUri+" has rolled the dice.", SLASH_DICE,playerUri);
				RestClient client = new RestClient();
				JSONService service = neededServices.get(ServiceNames.EVENT);
				if(service != null){
					String eventServiceUri = service.getUri(); 
					System.out.println("EventService-Uri: " + eventServiceUri);
					String code = client.sendCreateEventRequest(event, eventServiceUri);
					System.out.println("EventManager ReturnCode: " + code);
				}
			}
			return Dice.create(playerUri, gameUri);
		}, gson::toJson);
	}

	public Map<String, JSONService> getNeededServices() {
		return neededServices;
	}

	public void setNeededServices(Map<String, JSONService> neededServices) {
		this.neededServices = neededServices;
	}

}
