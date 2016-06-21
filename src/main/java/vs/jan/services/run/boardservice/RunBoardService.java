package vs.jan.services.run.boardservice;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import spark.Spark;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.services.yellowpages.RegisterService;

public class RunBoardService {

	public static void main(String[] args) throws UnknownHostException {
		
		boolean local = false;
		new BoardRESTApi();
		Spark.awaitInitialization();
		
		if(!local) {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			RegisterService.registerService("boards", ip, true);
		}

	}
}
