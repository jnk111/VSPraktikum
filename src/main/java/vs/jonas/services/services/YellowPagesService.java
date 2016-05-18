package vs.jonas.services.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.GetRestClient;
import vs.malte.json.ServiceArray;

public class YellowPagesService {
	
	private final String YELLOW_SERVICE_IP = "http://172.18.0.5:4567";
	private final String YELLOW_SERVICE_URL = "http://172.18.0.5:4567/services"; 
	private final String OF_NAME = "/of/name/JJMG"; 
	
	private final String YELLOW_SERVICE_IP_VON_AUSSEN = "https://141.22.34.15/cnt/172.18.0.5/4567";
	private final String YELLOW_SERVICE_URL_VON_AUSSEN = "https://141.22.34.15/cnt/172.18.0.5/4567/services";
	
	private Map<String, Service> services;
	
	public static final String ONLINE_SERVICES = "online";
	public static final String LOCAL_SERVICES = "local";
	public static final String CLIENT_SERVICES = "client";
	
	public YellowPagesService(String service_herkunft){
		try {
			initServices(service_herkunft);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public YellowPagesService(){
		try {
			initServices(ONLINE_SERVICES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initServices(String service_herkunft) throws IOException {
		services = new HashMap<>();
		
//		Je nachdem, ob local getestet werden soll oder im Docker-Container
		if(service_herkunft.equals(ONLINE_SERVICES)){
			fetchAllServices();
		}
		else if(service_herkunft.equals(CLIENT_SERVICES)){
			fetchClientServices();
		}
		else{
			fetchLocalServices();
		}
//		fetchHardcodedServices();
	}
	

	private void fetchClientServices() throws IOException {
		System.out.println("*** Fetch all Services ***");
		
		GetRestClient client = new GetRestClient();
		String resBody = client.get(YELLOW_SERVICE_URL_VON_AUSSEN+OF_NAME);
		
		// Die Liste aller Services (Uris) von uns
		ServiceArray services = new Gson().fromJson(resBody, ServiceArray.class);
		
		// Jeden Service anhand der uri von den YellowPages abfragen
		for(String uri : services.getServices()){
			String body = client.get(YELLOW_SERVICE_IP_VON_AUSSEN+uri);
			Service service = new Gson().fromJson(body, Service.class);
			this.services.put(service.getService(),service);
		}
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
		Service users = new Service("/services/383", "dice service", "JJMG", ServiceNames.USER, "running","http://172.18.0.60:4567/users");
		services.put(ServiceNames.USER, users);
		
		Service dice = new Service("/services/385", "dice service", "JJMG", ServiceNames.DICE, "running","http://172.18.0.59:4567/dice");
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
		
		new YellowPagesService(ONLINE_SERVICES);
	}
}
