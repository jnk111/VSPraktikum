package vs.jan.services.allocator;

import java.net.HttpURLConnection;
import com.google.gson.Gson;

import vs.jan.exception.ResponseCodeException;
import vs.jan.model.ServiceList;
import vs.jan.tools.HttpService;

public class ServiceAllocator {
	
	private final static Gson GSON = new Gson();	
	
	public static ServiceList initServices(String host, String gameid) 
			throws ResponseCodeException{
		
		String gameServiceUri = "http://" + host;
		gameServiceUri += "/games/" + gameid + "/services";
		String json = HttpService.get(gameServiceUri, HttpURLConnection.HTTP_OK);
		ServiceList list = GSON.fromJson(json, ServiceList.class);
		
		return list;
	};
	
}
