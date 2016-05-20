package vs.jonas.services;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
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
		try {
			neededServicesDice.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new DiceService(neededServicesDice).startService();
	}

}
