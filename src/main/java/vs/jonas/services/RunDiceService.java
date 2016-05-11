package vs.jonas.services;

import java.util.HashMap;
import java.util.Map;

import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.jonas.services.services.YellowPagesService;

public class RunDiceService {
	
	public static void main(String [] args){
		
//		Falls lokale Services verwendet werden:
//						Der Event-Service muss vor dem Dice-Service laufen
		new EventService().startService();

		YellowPagesService start = new YellowPagesService(YellowPagesService.LOCAL_SERVICES);
		
		Map<String, Service> neededServicesDice = new HashMap<>();
		neededServicesDice.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		new DiceService(neededServicesDice).startService();
	}


}
