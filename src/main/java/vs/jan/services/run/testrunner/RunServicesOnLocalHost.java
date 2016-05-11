package vs.jan.services.run.testrunner;

import java.util.HashMap;
import java.util.Map;

import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jan.services.boardservice.BoardRESTApi;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;

public class RunServicesOnLocalHost {

	public static void main(String[] args) {
		Map<String, Service> neededServicesDice = getNeededServices();
		new EventService().startService(); // Der EventService muss f�r den DiceService laufen
		new DiceService(neededServicesDice).startService();
		new BoardRESTApi();


	}
	
	private static Map<String, Service> getNeededServices() {
		Map<String, Service> services = new HashMap<>();
		
		// services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere

		Service s = new Service("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
				"http://localhost:4567/events");

		services.put(ServiceNames.EVENT, s);
		return services;
	}

}