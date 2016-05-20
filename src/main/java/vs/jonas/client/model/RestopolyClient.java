package vs.jonas.client.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ServiceUnavailableException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.services.services.YellowPagesService;
import vs.malte.json.GameDTO;

/**
 * Diese Klasse ist die Hauptkomponente fuer die Kommunikation mit den 
 * verschiedenen Services. 
 * 
 * Hier werden Anmeldevorgaenge und Abfragen getaetigt und von den Controllern
 * abgefragt.
 * 
 * @author Jones
 *
 */
public class RestopolyClient {

	private Service gameservice;
	
	public RestopolyClient(){
		YellowPagesService yellowPages = new YellowPagesService(YellowPagesService.ONLINE_SERVICES);
		try {
			gameservice = yellowPages.getService(ServiceNames.GAME);
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		RunAllServices.run(); // TODO nur zum testen
	}
	/**
	 * TODO Dummydata fuer Informationen ueber laufende Spiele
	 * @return
	 * @throws IOException 
	 * @throws UnirestException 
	 */
	public List<GameInformation> getGameInformations() throws IOException, UnirestException {
		List<GameInformation> data = new ArrayList<>();
		
		String uri = gameservice.getUri();
		String gameListResponse = get(uri);
		
		System.out.println("Antwort auf " + uri + ":\n" +gameListResponse);
		
		data.add(new GameInformation("/game/idasd","Monopoly-Dummy-Data","4"));
		return data;
	}

	public List<PlayerInformation> getPlayerInformations() {
		// TODO Auto-generated method stub
		List<PlayerInformation> data = new ArrayList<>();
		data.add(new PlayerInformation("dummy", "pawn", "2000", true));
		return data;
	}

	/**
	 * Liefert das W�rfelergebnis vom DiceService.
	 * Der DiceService muss laufen, damit ein Ergebnis errechnet werden kann.
	 * @return Das Wurfergebnis
	 * @throws IOException
	 */
	public int rollDice() throws IOException {
//		String uri = diceService.getUri();
//		String resBody = get(uri);
//		Dice dice = new Gson().fromJson(resBody, Dice.class);
//		
		return 666;//dice.getNumber();
	}
	
	public void createANewGame(String gameName) throws IOException{
		String uri = gameservice.getUri();
		GameDTO game = new GameDTO();
		game.setName(gameName);
		// TODO 
//		String resBody = postData(game, uri);
//		System.out.println("Antwort auf Post nach:" + uri + ": " + resBody );
	}

	/**
	 * Sendet ein Objekt an einen Service
	 * @param object Das Objekt, dass eingetragen werden soll.
	 * @param serviceUri 
	 * @return Den Statuscode der Anfrage.
	 * @throws IOException
	 * @throws UnirestException 
	 */
	public String postData(Object object, String serviceUri) throws IOException, UnirestException{
        Gson gson = new Gson();
        
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceUri)
                .header("accept", "application/json")
                .body(gson.toJson(object))
                .asJson();
		
		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		
//		System.out.println(responseObject.toString());
		
//		
//		URL url = new URL(serviceUri);
//		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		connection.setRequestMethod("POST");
//		connection.setDoOutput(true);
//		
//		String body = new Gson().toJson(object);
//
//		if(body != null){
//			connection.getOutputStream().write(body.getBytes());
//		}
//		
//		connection.connect(); //do it
//		
//		//Get response
//		int code = connection.getResponseCode();
////		String resBody = code < 400 ? IOUtils.toString(connection.getInputStream()) 
////				: IOUtils.toString(connection.getErrorStream());
		return "";
	}
	
	/**
	 * Schickt eine GET Abfrage an einen Service
	 * @param uri Die URI vom Service
	 * @return
	 * @throws IOException
	 * @throws UnirestException 
	 */
	public String get(String uri) throws IOException, UnirestException{
		Gson gson = new Gson();
		HttpResponse<JsonNode> jsonResponse = Unirest.get(uri)
                .header("accept", "application/json")
                .asJson();
		
		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		
//		URL url = new URL(uri);
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
		
		return responseObject.toString();
	}
}
