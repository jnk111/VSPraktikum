package vs.jan.services.run.boardservice;

import java.util.HashMap;
import java.util.Map;

import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;

public class RunBoardService {

	public static void main(String[] args) {
		BankService.run();
		Map<String, Service> neededServicesDice = getNeededServices(ServiceNames.DICE);
		new EventService().startService(); // Der EventService muss f�r den
																				// DiceService laufen
		new DiceService(neededServicesDice).startService();
		new UserServiceRESTApi();
		//new GameServiceFixed();
		new BoardRESTApi();
	}
	
	private static Map<String, Service> getNeededServices(String type) {
		Map<String, Service> services = new HashMap<>();

		// services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere

		if (type.equals(ServiceNames.DICE) || type.equals(ServiceNames.BOARD)) {
			Service s = new Service("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
					"http://localhost:4567/events");

			services.put(ServiceNames.EVENT, s);
		}

		return services;
	}
}
