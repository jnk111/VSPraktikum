package vs.jonas.services.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import com.google.gson.Gson;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.GetRestClient;
import vs.malte.json.ServiceArray;

public class YellowPagesService {

	private final String YELLOW_SERVICE_IP = "http://172.18.0.5:4567";
	private final String YELLOW_SERVICE_URL = "http://172.18.0.5:4567/services";
	private final String OF_NAME = "/of/name/JJMG";

	private Map<String, Service> services;

	private boolean online;

	/**
	 *
	 * @param online True, if you want to use the container-network
	 */
	public YellowPagesService(boolean online) {
		try {
			this.online = online;
			initServices(this.online);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public YellowPagesService() {
		try {
			initServices(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initServices(boolean online) throws IOException {
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
	 */
	private void fetchAllServices() throws IOException {
		System.out.println("*** Fetch all Services ***");

		GetRestClient client = new GetRestClient();
		String resBody = client.get(YELLOW_SERVICE_URL + OF_NAME);

		// Die Liste aller Services (Uris) von uns
		ServiceArray services = new Gson().fromJson(resBody, ServiceArray.class);

		// Jeden Service anhand der uri von den YellowPages abfragen
		for (String uri : services.getServices()) {
			String body = client.get(YELLOW_SERVICE_IP + uri);
			Service service = new Gson().fromJson(body, Service.class);
			this.services.put(service.getService(), service);
		}
		
		System.out.println(this.services);
	}

	public Map<String, Service> getAllServices() {
		return services;
	}

	public Service getService(String key) throws ServiceUnavailableException {
		Service result = null;
		if(this.services.containsKey(key)){
			result = this.services.get(key);
		} else{
			throw new ServiceUnavailableException("Der angefragte Service: " + key + " konnte nicht gefunden werden.");
		}
		return result;
	}

	private void fetchLocalServices() {
		Service games = new Service("/services/380", "FancyGameService", "JJMG", ServiceNames.GAME, "running",
				"http://localhost:4567/games");
		services.put(ServiceNames.GAME, games);
		//
		Service events = new Service("/services/381", "freshe events", "JJMG", ServiceNames.EVENT, "running",
				"http://localhost:4567/events");
		services.put(ServiceNames.EVENT, events);
		//
		// // Description fehlerhaft
		Service users = new Service("/services/382", "user service", "JJMG", ServiceNames.USER, "running",
				"http://localhost:4567/users");
		services.put(ServiceNames.USER, users);
		//
		Service dice = new Service("/services/383", "dice service", "JJMG", ServiceNames.DICE, "running",
				"http://localhost:4567/dice");
		services.put(ServiceNames.DICE, dice);

		Service board = new Service("/services/384", "board service", "JJMG", ServiceNames.BOARD, "running",
				"http://localhost:4567/boards");
		services.put(ServiceNames.BOARD, board);
	}

	@SuppressWarnings("unused")
	private void fetchHardcodedServices() {
		// Manuell eingef�gte uris
		Service games = new Service("/services/386", "FancyGameService", "JJMG", ServiceNames.GAME, "running",
				"http://172.18.0.48:4567/games");
		services.put(ServiceNames.GAME, games);

		Service events = new Service("/services/382", "freshe events", "JJMG", ServiceNames.EVENT, "running",
				"http://172.18.0.32:4567/events");
		services.put(ServiceNames.EVENT, events);

		// Description fehlerhaft
		Service users = new Service("/services/383", "dice service", "JJMG", ServiceNames.USER, "running",
				"http://172.18.0.60:4567/users");
		services.put(ServiceNames.USER, users);

		Service dice = new Service("/services/385", "dice service", "JJMG", ServiceNames.DICE, "running",
				"http://172.18.0.59:4567/dice");
		services.put(ServiceNames.DICE, dice);
	}

	public static void main(String[] args) {

		new YellowPagesService(true);
	}

	public String getBaseIP() {
		if(online){
			return YELLOW_SERVICE_IP;
		}
		else return "http://localhost:4567";
	}
}
