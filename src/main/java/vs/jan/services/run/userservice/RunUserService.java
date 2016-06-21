package vs.jan.services.run.userservice;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.mashape.unirest.http.exceptions.UnirestException;

import spark.Spark;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.services.yellowpages.RegisterService;

public class RunUserService {
	public static void main(String[] args) throws UnknownHostException, UnirestException {
		
		new UserServiceRESTApi();
		
		Spark.awaitInitialization();
		
		String ip = Inet4Address.getLocalHost().getHostAddress();
		
		RegisterService.registerService("users", ip, true);
	}
}
