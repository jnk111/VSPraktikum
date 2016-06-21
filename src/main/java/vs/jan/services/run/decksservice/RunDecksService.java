package vs.jan.services.run.decksservice;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import spark.Spark;
import vs.jan.api.decksservice.DecksAPI;
import vs.jan.services.yellowpages.RegisterService;

public class RunDecksService {

	public static void main(String[] args) throws UnknownHostException {
		boolean local = true;
		new DecksAPI();
		
		Spark.awaitInitialization();
		
		if(!local) {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			RegisterService.registerService("decks", ip, true);
		}

	}
}
