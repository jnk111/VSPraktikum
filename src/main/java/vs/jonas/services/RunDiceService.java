package vs.jonas.services;

import java.util.HashMap;
import java.util.Map;

import vs.jan.Service;
import vs.jan.ServiceNames;
import vs.jan.YellowPagesService;
import vs.jonas.services.services.DiceService;

public class RunDiceService {
	
	public static void main(String [] args){
		
//		Falls lokale Services verwendet werden:
//						Der Event-Service muss vor dem Dice-Service laufen
//		new EventService().startService();

		YellowPagesService start = new YellowPagesService();
		
		Map<String, Service> neededServicesDice = new HashMap<>();
		neededServicesDice.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		new DiceService(neededServicesDice).startService();
	}


}
