package vs.jan.services.run.boardservice;

import java.util.HashMap;
import java.util.Map;

import vs.gerriet.controller.bank.BanksController;
import vs.gerriet.service.BankService;
import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jan.services.boardservice.BoardRESTApi;
import vs.jan.services.userservice.UserServiceRESTApi;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.malte.services.GamesService;

public class RunBoardService {

	public static void main(String[] args) {
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
