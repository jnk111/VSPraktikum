package vs.jan.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import vs.jan.exceptions.ConnectionRefusedException;
import vs.jan.exceptions.InvalidInputException;

public class HttpService{
	
	private static final Gson GSON = new Gson();
	
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
	
	public static String get(String URL, int expResponseCode) {
		HttpURLConnection connection = connect("GET", URL, null, expResponseCode);
		return doGet(connection, URL, expResponseCode);
	}

	private static String doGet(HttpURLConnection connection, String uRL, int expResponseCode) {
		try {
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == expResponseCode) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				return response.toString();
			}
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}
		return null;
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
