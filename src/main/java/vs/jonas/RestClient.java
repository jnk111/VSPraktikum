package vs.jonas;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RestClient {

	
	/**
	 * Sendet ein Objekt an einen Service
	 * 
	 * @param object
	 *            Das Objekt, dass eingetragen werden soll.
	 * @param serviceUri
	 * @return Den Statuscode der Anfrage.
	 * @throws IOException
	 * @throws UnirestException
	 */
	public static JsonObject postData(Object object, String serviceUri) throws IOException, UnirestException {
		Gson gson = new Gson();
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceUri).header("accept", "application/json")
				.body(gson.toJson(object)).asJson();

		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		return responseObject;
	}

	/**
	 * Schickt eine GET Abfrage an einen Service
	 * 
	 * @param uri
	 *            Die URI vom Service
	 * @return
	 * @throws IOException
	 * @throws UnirestException
	 */
	public static JsonObject get(String uri) throws IOException, UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get(uri).header("accept", "application/json").asJson();

		return new Gson().fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
	}
	
//	public String get(String _url) throws IOException{
//		URL url = new URL(_url);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setRequestMethod("GET");
//		conn.setDoInput(true);
//		
//		conn.setRequestProperty("Accept", "application/json");
//		
//		conn.connect(); //do it
//		int code = conn.getResponseCode(); 
//		String resBody = code < 400 ? IOUtils.toString(conn.getInputStream()) 
//		: IOUtils.toString(conn.getErrorStream());
////		System.out.println(resBody);
//		conn.disconnect();
//		
//		return resBody;
//	}
}
