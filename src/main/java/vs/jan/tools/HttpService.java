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
import vs.jan.exceptions.ResourceNotFoundException;

public class HttpService {

	private static final Gson GSON = new Gson();

	public static void put(String URL, Object body, int expResponseCode) {
		HttpURLConnection connection = connect("PUT", URL, body, expResponseCode);
		update(connection, body, expResponseCode);
	}

	public static void post(String URL, Object body, int expResponseCode) {
		HttpURLConnection connection = connect("POST", URL, body, expResponseCode);
		update(connection, body, expResponseCode);
	}

	public static void delete(String URL, int expResponseCode) {
		HttpURLConnection connection = connect("DELETE", URL, null, expResponseCode);
		doDelete(connection, URL, expResponseCode);
	}

	private static void doDelete(HttpURLConnection connection, String uRL, int expResponseCode) {

		try{
			connection.setRequestMethod("DELETE");
			int responseCode = connection.getResponseCode();
			System.out.println("GOT RESPONSE CODE: " + responseCode);
			if (responseCode != expResponseCode) {
				throw new ResourceNotFoundException();
			}
		}catch(IOException ioe){
			throw new ConnectionRefusedException();
		}


	}

	public static String get(String URL, int expResponseCode) {
		HttpURLConnection connection = connect("GET", URL, null, expResponseCode);
		return doGet(connection, URL, expResponseCode);
	}

	private static void update(HttpURLConnection connection, Object body, int expResponseCode) {

		try {
			
			if(body != null){
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				String json = GSON.toJson(body);
				wr.writeBytes(json);
				wr.flush();
				wr.close();
			}

			int respCode = connection.getResponseCode();
			System.out.println("GOT RESPONSE CODE: " + respCode);
			if (respCode != expResponseCode) {
				throw new InvalidInputException();
			}
		} catch (IOException ioe) {
			throw new InvalidInputException();
		}
	}

	private static String doGet(HttpURLConnection connection, String uRL, int expResponseCode) {
		try {
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			System.out.println("GOT RESPONSE CODE: " + responseCode);
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

	public static HttpURLConnection connect(String method, String URL, Object body, int expResponseCode) {
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
