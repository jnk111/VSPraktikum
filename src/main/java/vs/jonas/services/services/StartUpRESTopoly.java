package vs.jonas.services.services;

import java.util.HashMap;
import java.util.Map;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.malte.services.GamesService;

/**
 * TODO: implement
 * 
 * @author Jan Dieckhoff
 *
 */
public class StartUpRESTopoly {

	public static void main(String[] args) {

		YellowPagesService start = new YellowPagesService();
		Map<String, Service> neededServicesDice = getNeededServices(start);
		
		new EventService().startService(); // Der EventService muss f�r den DiceService laufen
		new DiceService(neededServicesDice);
		new GamesService();

	}

	private static Map<String, Service> getNeededServices(YellowPagesService start) {
		Map<String, Service> services = new HashMap<>();
		
		// services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere

		Service s = new Service("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
				"http://localhost:4567/events");

		services.put(ServiceNames.EVENT, s);
		return services;
	}

}
