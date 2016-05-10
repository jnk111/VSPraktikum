package vs.jonas;

import java.util.HashMap;
import java.util.Map;

import vs.jan.Service;
import vs.jan.ServiceNames;
import vs.jan.YellowPagesService;

public class RunDiceService {
	
	public static void main(String [] args){
		YellowPagesService start = new YellowPagesService();
		Map<String, Service> neededServicesDice = getNeededServices(start);
		new DiceService(neededServicesDice).startService();
		new EventService().startService();
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
