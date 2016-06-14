package vs.jonas.services.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jonas.RestClient;
import vs.malte.json.ServiceArray;

public class YellowPagesService {

	private final String YELLOW_SERVICE_IP = "http://172.18.0.5:4567";
	private final String YELLOW_SERVICE_URL = "http://172.18.0.5:4567/services";
	private final String OF_NAME = "/of/name/JJMG";
	private String GAMES_BASE_URL;

	private Map<String, JSONService> services;

	private boolean online;

	/**
	 *
	 * @param online True, if you want to use the container-network
	 */
	public YellowPagesService(boolean online) {
		try {
			this.online = online;
			GAMES_BASE_URL = "http://172.18.0.78:4567";
			initServices(this.online);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public YellowPagesService(){
		try {
			initServices(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initServices(boolean online) throws IOException, UnirestException {
		services = new HashMap<>();

		// Je nachdem, ob local getestet werden soll oder im Docker-Container
		if (online) {
			fetchAllServices();
		} else {
			fetchLocalServices();
		}
		// fetchHardcodedServices();
	}

	/**
	 * Holt sich alle angemeldeten Services
	 * 
	 * @throws IOException
	 * @throws UnirestException 
	 */
	private void fetchAllServices() throws IOException, UnirestException {
		System.out.println("*** Fetch all Services ***");

		JsonObject resBody = RestClient.get(YELLOW_SERVICE_URL + OF_NAME);

		// Die Liste aller Services (Uris) von uns
		ServiceArray services = new Gson().fromJson(resBody.toString(), ServiceArray.class);

		// Jeden Service anhand der uri von den YellowPages abfragen
		for (String uri : services.getServices()) {
			JsonObject body = RestClient.get(YELLOW_SERVICE_IP + uri);
			JSONService service = new Gson().fromJson(body.toString(), JSONService.class);
//			System.out.println("Fetch Service: " + service.getService());
			this.services.put(service.getService(), service);
		}
		
		System.out.println(this.services);
	}

	public Map<String, JSONService> getAllServices() {
		return services;
	}

	public JSONService getService(String key) throws ServiceUnavailableException {
		JSONService result = null;
		if(this.services.containsKey(key)){
			result = this.services.get(key);
		} else{
			throw new ServiceUnavailableException("Der angefragte Service: " + key + " konnte nicht gefunden werden.");
		}
		return result;
	}

	private void fetchLocalServices() {
		JSONService games = new JSONService("/services/380", "FancyGameService", "JJMG", ServiceNames.GAME, "running",
				"http://localhost:4567/games");
		services.put(ServiceNames.GAME, games);
		//
		JSONService events = new JSONService("/services/381", "freshe events", "JJMG", ServiceNames.EVENT, "running",
				"http://localhost:4567/events");
		services.put(ServiceNames.EVENT, events);
		//
		// // Description fehlerhaft
		JSONService users = new JSONService("/services/382", "user service", "JJMG", ServiceNames.USER, "running",
				"http://localhost:4567/users");
		services.put(ServiceNames.USER, users);
		//
		JSONService dice = new JSONService("/services/383", "dice service", "JJMG", ServiceNames.DICE, "running",
				"http://localhost:4567/dice");
		services.put(ServiceNames.DICE, dice);

		JSONService board = new JSONService("/services/384", "board service", "JJMG", ServiceNames.BOARD, "running",
				"http://localhost:4567/boards");
		services.put(ServiceNames.BOARD, board);
		
		JSONService broker = new JSONService("/services/385", "broker service" , "JJMG", ServiceNames.BROKER, "running",
				"http://localhost:4567/broker");
		services.put(ServiceNames.BROKER, broker);
	}

	@SuppressWarnings("unused")
	private void fetchHardcodedServices() {
		// Manuell eingef�gte uris
		JSONService games = new JSONService("/services/386", "FancyGameService", "JJMG", ServiceNames.GAME, "running",
				"http://172.18.0.48:4567/games");
		services.put(ServiceNames.GAME, games);

		JSONService events = new JSONService("/services/382", "freshe events", "JJMG", ServiceNames.EVENT, "running",
				"http://172.18.0.32:4567/events");
		services.put(ServiceNames.EVENT, events);

		// Description fehlerhaft
		JSONService users = new JSONService("/services/383", "dice service", "JJMG", ServiceNames.USER, "running",
				"http://172.18.0.60:4567/users");
		services.put(ServiceNames.USER, users);

		JSONService dice = new JSONService("/services/385", "dice service", "JJMG", ServiceNames.DICE, "running",
				"http://172.18.0.59:4567/dice");
		services.put(ServiceNames.DICE, dice);
	}

	public static void main(String[] args) {

		new YellowPagesService(true);
	}

	public String getBaseIP() {
		if(online){
			return GAMES_BASE_URL;
		}
		else return "http://localhost:4567";
	}
}
