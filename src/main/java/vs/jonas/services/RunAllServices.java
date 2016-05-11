package vs.jonas.services;

import java.util.HashMap;
import java.util.Map;

import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.jonas.services.services.YellowPagesService;
import vs.malte.services.GamesService;

public class RunAllServices {

	
	public static void run(){
		

		YellowPagesService start = new YellowPagesService(YellowPagesService.LOCAL_SERVICES);
		
		new GamesService();
		new EventService().startService();
		
		Map<String, Service> neededServicesDice = new HashMap<>();
		neededServicesDice.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		new DiceService(neededServicesDice).startService();
	}

}
