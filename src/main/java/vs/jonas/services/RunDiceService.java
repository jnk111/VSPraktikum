package vs.jonas.services;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ServiceUnavailableException;

import spark.Spark;
import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jan.services.yellowpages.RegisterService;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.YellowPagesService;

public class RunDiceService {
	
	public static void main(String [] args) throws UnknownHostException{
		
//		Falls lokale Services verwendet werden:
//						Der Event-Service muss vor dem Dice-Service laufen
//		new EventService().startService();

		boolean local = true;
		
		YellowPagesService start = new YellowPagesService(false);
		
		System.out.println("Created YellowPagesService succesfully");
		Map<String, JSONService> neededServicesDice = new HashMap<>();
		
		try {
			neededServicesDice.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new DiceService(neededServicesDice).startService();
		Spark.awaitInitialization();
		
		// Hier aufpassen -> sonst wird lokaler Service angemeldet (nur im VPN)
		if(!local) {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			RegisterService.registerService("dice", ip, true);
		}

	}


}
