package vs.jonas.client.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jonas.client.json.CreateGame;
import vs.jonas.client.json.GameResponse;
import vs.jonas.client.json.PlayerList;
import vs.jonas.client.json.PlayerResponse;
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
    private String BASE_URL;
	private Gson gson;
	
	public RestopolyClient(YellowPagesService yellowPages){
		try {
            BASE_URL = yellowPages.getBaseIP();
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
			System.out.println(gamesList.get(i));
            game.setNumberOfPlayers(5);
            data.add(game);
		}
		return data;
	}

	public List<Player> getPlayerInformations(String gameID) throws IOException, UnirestException {
        System.out.println("**************  Get Players **************");
		List<Player> data = new ArrayList<>();

        String gameServiceUri = gameservice.getUri();
        // http://localhost:4567/games/100/players
        String gamesPlayersUri = gameServiceUri + "/" + gameID + "/players";
        JsonObject playerListResponse = get(gamesPlayersUri);

        System.out.println("Antwort-PlayerList durch die URL: " + gamesPlayersUri + ":\n" +playerListResponse.toString());

        PlayerList playerList = gson.fromJson(playerListResponse, PlayerList.class);
        for(String playerUri : playerList.getPlayers()){

            // {"ready":false,"id":"/games/100/players/wario","user":"/user/wario"}
            JsonObject playerRessource = get(BASE_URL+playerUri);
            System.out.println("Antwort auf " + BASE_URL+playerUri + ":\n" +playerRessource.toString());

            //
            PlayerResponse player =  gson.fromJson(playerRessource, PlayerResponse.class);

            // TODO CheckPlayer for null-values
            // TODO TableModel erweitern um Usernamen
            // TODO Spieler anmelden
            // TODO Würfeln

            String name = "";
            String pawn = "";
            String account ="";
            String ready = "";

            if(checkNotNull(player.getId())){
                name = gson.fromJson(get(BASE_URL + player.getId()).get("id"), String.class);
            }
//
//            if(checkNotNull(player.getPawn())){
//                System.out.println(BASE_URL + player.getPawn());
//                pawn = gson.fromJson(get(BASE_URL + player.getPawn()),String.class);
//            }
//
//            if(checkNotNull(player.getAccount())){
//                account = gson.fromJson(get(BASE_URL + player.getPawn()),String.class);
//            }
//
//            if(checkNotNull(player.getReady())){
//                System.out.println(get(BASE_URL + player.getReady()));
//                ready = gson.fromJson(get(BASE_URL + player.getReady()),String.class);
//            }
            data.add(new Player(name,pawn,account,ready));

        }

		data.add(new Player("dummy", "pawn", "2000", "true"));
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

    private boolean checkNotNull(Object object){
        if(object == null){
            return false;
        }
        return true;
    }
}
