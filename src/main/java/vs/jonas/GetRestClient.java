package vs.jonas;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import spark.utils.IOUtils;

public class GetRestClient {

	public String get(String _url) throws IOException{
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		
		conn.setRequestProperty("Accept", "application/json");
		
		conn.connect(); //do it
		int code = conn.getResponseCode(); 
		String resBody = code < 400 ? IOUtils.toString(conn.getInputStream()) 
		: IOUtils.toString(conn.getErrorStream());
//		System.out.println(resBody);
		conn.disconnect();
		
		return resBody;
	}
}
