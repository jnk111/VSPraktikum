package vs.jan.services.run.brokerservice;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import spark.Spark;
import vs.jan.api.broker.BrokerAPI;
import vs.jan.services.yellowpages.RegisterService;

public class RunBrokerService {

	public static void main(String[] args) throws UnknownHostException {
		boolean local = true;
		
		new BrokerAPI();
		Spark.awaitInitialization();
		
		if(!local) {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			RegisterService.registerService("broker", ip, true);
		}

	}
}
