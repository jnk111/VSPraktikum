package vs.aufgabe1b;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import vs.aufgabe1b.models.Event;

public class RestClient {
	
	/**
	 * Sendet eine Anfrage an den EventManager, um ein neues Event anzumelden.
	 * @param event Das Event, dass eingetragen werden soll.
	 * @return Den Statuscode der Anfrage.
	 * @throws IOException
	 */
	public String sendCreateEventRequest(Event event) throws IOException{
		URL url = new URL("http://localhost:4567/events");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		
		String body = new Gson().toJson(event);

		if(body != null){
			connection.getOutputStream().write(body.getBytes());
		}
		
		connection.connect(); //do it
		
		//Get response
		int code = connection.getResponseCode();
//		String resBody = code < 400 ? IOUtils.toString(connection.getInputStream()) 
//				: IOUtils.toString(connection.getErrorStream());
		return code+"";
	}
}
