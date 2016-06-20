package vs.jan.services.allocator;

import java.net.HttpURLConnection;
import com.google.gson.Gson;

import vs.jan.exception.ResponseCodeException;
import vs.jan.model.ServiceList;
import vs.jan.tools.HttpService;

public class ServiceAllocator {
	
	private final static Gson GSON = new Gson();	
	
	public static ServiceList initServices(String serviceUri, String gameid) 
			throws ResponseCodeException{
		
		System.out.println("Gameservice URI: " + serviceUri);
		String json = HttpService.get(serviceUri, HttpURLConnection.HTTP_OK);
		ServiceList list = GSON.fromJson(json, ServiceList.class);
		System.out.println("LISTE: " + list);
		// Temp
		list.setClient("http://localhost:4567/client");
		list.initializeHosts();
		return list;
	};
	
}
