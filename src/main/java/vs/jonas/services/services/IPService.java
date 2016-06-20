package vs.jonas.services.services;

import java.net.InetAddress;
import java.net.UnknownHostException;

import spark.Spark;

public class IPService {

	public void startService() throws UnknownHostException{
		
		
		String ip = InetAddress.getLocalHost().getHostAddress();
		System.out.println(ip);
		Spark.ipAddress(ip);
		
		Spark.get("/ip", (req,res) -> {
			System.out.println(req.host());
			return req.ip();
		});
	}
	
	public static void main(String[] args) throws UnknownHostException {
		new IPService().startService();
	}
}
