package vs.jan.services.yellowpageservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jonas.GetRestClient;
import vs.malte.ServiceArray;

public class YellowPagesService {
	
	private final String YELLOW_SERVICE_IP = "http://172.18.0.5:4567";
	private final String YELLOW_SERVICE_URL = "http://172.18.0.5:4567/services"; 
	private final String OF_NAME = "/of/name/JJMG"; 
	
	private Map<String, Service> services;
	
	public YellowPagesService(){

		initServices();
	}

	private void initServices() {
		services = new HashMap<>();
		
//		Von den folgenden drei Methoden nur eine Ausw�hlen:
		try {
			fetchAllServices();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		fetchHardcodedServices();
//		fetchLocalServices();
	}
	

	/**
	 * Holt sich alle angemeldeten Services 
	 * @throws IOException 
	 */
	private void fetchAllServices() throws IOException {
		System.out.println("*** Fetch all Services ***");
		
		GetRestClient client = new GetRestClient();
		String resBody = client.get(YELLOW_SERVICE_URL+OF_NAME);
		
		// Die Liste aller Services (Uris) von uns
		ServiceArray services = new Gson().fromJson(resBody, ServiceArray.class);
		
		// Jeden Service anhand der uri von den YellowPages abfragen
		for(String uri : services.getServices()){
			String body = client.get(YELLOW_SERVICE_IP+uri);
			Service service = new Gson().fromJson(body, Service.class);
			this.services.put(service.getService(),service);
		}
		System.out.println(this.services);
	}

	public Map<String, Service> getAllServices() {
		return services;
	}

	public Service getService(String key) {
		return this.services.get(key);
	}
	
	
	private void fetchHardcodedServices() {
		// Manuell eingef�gte uris
		Service games =  new Service("/services/386", "FancyGameService", "JJMG", ServiceNames.GAME, "running", "http://172.18.0.48:4567/games");
		services.put(ServiceNames.GAME,games);
		
		Service events = new Service("/services/382", "freshe events", "JJMG", ServiceNames.EVENT, "running", "http://172.18.0.32:4567/events");
		services.put(ServiceNames.EVENT, events);
		
		// Description fehlerhaft
		Service users = new Service("/services/383", "dice service", "JJMG", ServiceNames.USER, "running","http://172.18.0.35:4567/users");
		services.put(ServiceNames.USER, users);
		
		Service dice = new Service("/services/385", "dice service", "JJMG", ServiceNames.DICE, "running","http://172.18.0.44:4567/dice");
		services.put(ServiceNames.DICE, dice);
	}
	
	private void fetchLocalServices() {
		Service games =  new Service("/services/386", "FancyGameService", "JJMG", ServiceNames.GAME, "running", "http://localhost:4567/games");
		services.put(ServiceNames.GAME,games);
//		
		Service events = new Service("/services/382", "freshe events", "JJMG", ServiceNames.EVENT, "running", "http://localhost:4567/events");
		services.put(ServiceNames.EVENT, events);
//		
//		// Description fehlerhaft
		Service users = new Service("/services/383", "dice service", "JJMG", ServiceNames.USER, "running","http://localhost:4567/users");
		services.put(ServiceNames.USER, users);
//		
		Service dice = new Service("/services/385", "dice service", "JJMG", ServiceNames.DICE, "running","http://localhost:4567/dice");
		services.put(ServiceNames.DICE, dice);
	}

	public static void main(String[] args) {
		new YellowPagesService();
	}
}
