package vs.jan.services.allocator;

import java.net.HttpURLConnection;
import com.google.gson.Gson;

import vs.jan.model.ServiceList;
import vs.jan.tools.HttpService;

public class ServiceAllocator {
	
	private final static Gson GSON = new Gson();	
	
	public static ServiceList initServices(String host, String gameid){
		
		String gameServiceUri = "http://" + host;
		gameServiceUri += "/games/" + gameid + "/services";
		String list = HttpService.get(gameServiceUri, HttpURLConnection.HTTP_OK);
		return GSON.fromJson(list, ServiceList.class);
	};
	
}
