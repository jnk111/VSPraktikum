package vs.jan.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import vs.jan.exceptions.ConnectionRefusedException;
import vs.jan.exceptions.InvalidInputException;

public class HttpService {
	
	private final static Gson GSON = new Gson();

	public static void post(String URL, Object body, int expResponseCode) {
		HttpURLConnection connection = connect("POST", URL, body, expResponseCode);
		doPost(connection, body, expResponseCode);
	}

	private static void doPost(HttpURLConnection connection, Object body, int expResponseCode) {
		
		try{
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String json = GSON.toJson(body);
			wr.writeBytes(json);
			wr.flush();
			wr.close();

			int respCode = connection.getResponseCode();
			if(respCode != expResponseCode){
				throw new InvalidInputException();
			}
		}catch(IOException ioe){
			throw new InvalidInputException();
		}
	}
		
	

	public static void get(String URL, int expResponseCode) {
		HttpURLConnection connection = connect("GET", URL, null, expResponseCode);
	}

	public static HttpURLConnection connect(String method, String URL, 
																					Object body, int expResponseCode) {
		try {
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			return connection;
		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}
	}

}
