package vs.jonas.client.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import spark.utils.IOUtils;
import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jonas.services.RunAllServices;
import vs.jonas.services.model.Dice;
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

	private YellowPagesService yellowPages;
	private Service gameservice;
	private Service diceService;
	
	public RestopolyClient(){
		yellowPages = new YellowPagesService(YellowPagesService.CLIENT_SERVICES);
		gameservice = yellowPages.getService(ServiceNames.GAME);
		diceService = yellowPages.getService(ServiceNames.DICE);
		
		RunAllServices.run(); // TODO nur zum testen
	}
	/**
	 * TODO Dummydata fuer Informationen ueber laufende Spiele
	 * @return
	 * @throws IOException 
	 */
	public List<GameInformation> getGameInformations() throws IOException {
		List<GameInformation> data = new ArrayList<>();
		
		String uri = gameservice.getUri();
		String gameListResponse = get(uri);
		
		System.out.println("Antwort auf " + uri + ":" +gameListResponse);
		
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
		String uri = diceService.getUri();
		String resBody = get(uri);
		Dice dice = new Gson().fromJson(resBody, Dice.class);
		
		return dice.getNumber();
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
	 */
	public String postData(Object object, String serviceUri) throws IOException{
		URL url = new URL(serviceUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		
		String body = new Gson().toJson(object);

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
	
	/**
	 * Schickt eine GET Abfrage an einen Service
	 * @param uri Die URI vom Service
	 * @return
	 * @throws IOException
	 */
	public String get(String uri) throws IOException{
		URL url = new URL(uri);
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
