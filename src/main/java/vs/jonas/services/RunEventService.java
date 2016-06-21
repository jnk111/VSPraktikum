package vs.jonas.services;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import spark.Spark;
import vs.jan.services.yellowpages.RegisterService;
import vs.jonas.services.services.EventService;

public class RunEventService {

	public static void main(String[] args) throws UnknownHostException{
		boolean local = true;
		new EventService().startService();
		Spark.awaitInitialization();
		
		// Hier aufpassen -> sonst wird lokaler Service angemeldet (nur im VPN)
		if(!local) {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			RegisterService.registerService("events", ip, true);
		}

	}
}
