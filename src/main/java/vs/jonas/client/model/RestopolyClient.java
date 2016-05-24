package vs.jonas.client.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.client.json.CreateGame;
import vs.jonas.client.json.GameResponse;
import vs.jonas.services.services.YellowPagesService;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private Gson gson;
	
	public RestopolyClient(YellowPagesService yellowPages){
		try {
			gameservice = yellowPages.getService(ServiceNames.GAME);
			gson = new Gson();
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		RunAllServices.run(); // TODO nur zum testen
	}
	/**
	 * Laedt die aktuellen Spiele
	 * @return
	 * @throws IOException 
	 * @throws UnirestException 
	 */
	public List<GameResponse> getGameResponses() throws IOException, UnirestException {
		List<GameResponse> data = new ArrayList<>();
		
		String uri = gameservice.getUri();
		JsonObject gameListResponse = get(uri);
		
		System.out.println("Antwort auf " + uri + ":\n" +gameListResponse.toString());

		JsonArray gamesList = gameListResponse.getAsJsonArray("games");

		for (int i=0; i< gamesList.size(); i++){
			GameResponse game = gson.fromJson(gamesList.get(i), GameResponse.class);
			System.out.println(game);
			System.out.println("TADAAAA:\n"+gamesList.get(i));
            data.add(game);
		}
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
	
	public void createANewGame(String gameName) throws IOException, UnirestException {
		String uri = gameservice.getUri();
		CreateGame game = new CreateGame(gameName);

		// TODO
		JsonObject resBody = postData(game, uri);
		System.out.println("Antwort auf Post nach:" + uri + ": " + resBody.toString());
	}

	/**
	 * Sendet ein Objekt an einen Service
	 * @param object Das Objekt, dass eingetragen werden soll.
	 * @param serviceUri 
	 * @return Den Statuscode der Anfrage.
	 * @throws IOException
	 * @throws UnirestException 
	 */
	public JsonObject postData(Object object, String serviceUri) throws IOException, UnirestException{
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceUri)
                .header("accept", "application/json")
                .body(gson.toJson(object))
                .asJson();
		
		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		return responseObject;
	}
	
	/**
	 * Schickt eine GET Abfrage an einen Service
	 * @param uri Die URI vom Service
	 * @return
	 * @throws IOException
	 * @throws UnirestException 
	 */
	public JsonObject get(String uri) throws IOException, UnirestException{
		HttpResponse<JsonNode> jsonResponse = Unirest.get(uri)
                .header("accept", "application/json")
                .asJson();
		
		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		return responseObject;
	}
}
