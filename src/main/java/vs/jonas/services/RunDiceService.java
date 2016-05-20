package vs.jonas.services;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.YellowPagesService;

public class RunDiceService {
	
	public static void main(String [] args){
		
//		Falls lokale Services verwendet werden:
//						Der Event-Service muss vor dem Dice-Service laufen
//		new EventService().startService();

		YellowPagesService start = new YellowPagesService(YellowPagesService.ONLINE_SERVICES);
		
		System.out.println("Created YellowPagesService succesfully");
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
