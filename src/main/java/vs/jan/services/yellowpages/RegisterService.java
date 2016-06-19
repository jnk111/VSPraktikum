package vs.jan.services.yellowpages;

import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.model.yellowpages.JSONGETService;
import vs.jan.model.yellowpages.JSONPOSTService;
import vs.jan.model.yellowpages.JSONServiceURIs;
import vs.jan.tools.HttpService;

public class RegisterService {

	private final static Gson CONVERT = new Gson();
	private final static String GROUP_NAME = "JJMG";
	private final static String YELLOW_HOST_URI = "http://172.18.0.5:4567";
	private final static String SERVICE_URI = YELLOW_HOST_URI + "/services";
	private final static String SO_NAME_URI = SERVICE_URI + "/of/name";
	private final static String SERVICE_REQ_URI = SO_NAME_URI + "/" + GROUP_NAME;

	public static void registerService(String type, String serviceUri, boolean delete) throws UnknownHostException, UnirestException {
		
		String json = HttpService.get(SERVICE_REQ_URI, HttpURLConnection.HTTP_OK);
		JSONServiceURIs uris = CONVERT.fromJson(json, JSONServiceURIs.class);
		
		boolean success = false;
		if (delete) success = tryDeleteOldService(uris, type);
		
		if (success)
			postService(type, serviceUri);
	}

	private static void postService(String type, String serviceUri) throws UnknownHostException {
		JSONPOSTService post = new JSONPOSTService(GROUP_NAME, type, type, serviceUri);
		HttpService.post(SERVICE_URI, post, HttpURLConnection.HTTP_CREATED);

	}

	public static boolean tryDeleteOldService(JSONServiceURIs uris, String type) {

		try {
			for (String uri : uris.getServices()) {
				System.out.println(YELLOW_HOST_URI + uri);
				String jsonUri = HttpService.get(uri, HttpURLConnection.HTTP_OK);
				JSONGETService src = CONVERT.fromJson(jsonUri, JSONGETService.class);

				if (src.getService().equals(type)) {
					HttpService.delete(SERVICE_URI + "/" + getId(uri), HttpURLConnection.HTTP_OK);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
		return true;
	}

	private static String getId(String uri) {
		String[] u = uri.split("/");
		return u[u.length - 1];
	}

}
