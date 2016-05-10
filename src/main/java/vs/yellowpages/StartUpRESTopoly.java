package vs.yellowpages;

import java.util.HashMap;
import java.util.Map;

import vs.aufgabe1.diceservice.DiceService;
import vs.yellowpages.models.Service;

/**
 * TODO: implement
 * @author Jan Dieckhoff
 *
 */
public class StartUpRESTopoly {
	
	public static void main(String [] args){
		
		YellowPagesService start = new YellowPagesService();
		Map<String, Service> neededServices = getNeededServices(start);
		new DiceService(neededServices);
		
	}

	private static Map<String, Service> getNeededServices(YellowPagesService start) {
		Map<String, Service> services = new HashMap<>();
		services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere
		return services;
	}

}
