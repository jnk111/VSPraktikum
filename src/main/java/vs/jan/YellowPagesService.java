package vs.jan;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import spark.utils.IOUtils;

public class YellowPagesService {

	/**
	 * Die URL zu unseren Service Uris
	 */
	private String service_uris; 
	private String yellowPageURL; 
	
	private Map<String, Service> services;
	
	public YellowPagesService(){

		initServices();
	}

	private void initServices() {
		services = new HashMap<>();
		
//		Sobald das über den Docker Container läuft:
		service_uris = "http://172.18.0.5:4567/services/of/name/JJMG";
		yellowPageURL = "http://172.18.0.5:4567/services/"; 
		
//		Von den folgenden drei Methoden nur eine Auswählen:
		fetchAllServices(); 
//		fetchHardcodedServices();
//		fetchLocalServices();
	}
	

	/**
	 * Holt sich alle angemeldeten Services 
	 */
	private void fetchAllServices() {
		System.out.println("*** Fetch all Services ***");
		try {
			URL url = new URL(service_uris);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			conn.setRequestProperty("Accept", "application/json");
			
			conn.connect(); //do it
			int code = conn.getResponseCode(); 
			String resBody = code < 400 ? IOUtils.toString(conn.getInputStream()) 
			: IOUtils.toString(conn.getErrorStream());
			System.out.println(resBody);
			conn.disconnect();
			
			/*
			 * Hier könnte man jetzt den resBody in per GSON deserialisieren und würde dann 
			 * in der Theorie ein Array mit den Uris unserer Services erhalten.
			 * 
			 * Mit der URI kann man dann die yellowPageUrl ansprechen und würde dann Informationen über den
			 * einzelnen Service erhalten, so dass wir die Services dann in die Map einfügen können.
			 */
			// ServiceList services = new Gson().fromJson(resBody, ServiceList.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, Service> getAllServices() {
		return services;
	}

	public Service getService(String key) {
		return this.services.get(key);
	}
	
	
	private void fetchHardcodedServices() {
		// Manuell eingefügte uris
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
