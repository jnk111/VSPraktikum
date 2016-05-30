package vs.jonas.client.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ServiceUnavailableException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jonas.client.json.Account;
import vs.jonas.client.json.Board;
import vs.jonas.client.json.CreateGame;
import vs.jonas.client.json.DiceRolls;
import vs.jonas.client.json.Field;
import vs.jonas.client.json.GameResponse;
import vs.jonas.client.json.Pawn;
import vs.jonas.client.json.PawnList;
import vs.jonas.client.json.Place;
import vs.jonas.client.json.PlayerID;
import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.json.PlayerList;
import vs.jonas.client.json.PlayerResponse;
import vs.jonas.client.json.User;
import vs.jonas.services.model.Dice;
import vs.jonas.services.services.YellowPagesService;

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

	private JSONService gameService;
	private JSONService boardService;
	private String BASE_URL;
	private Gson gson;
	private User user;

	/**
	 * Initialisiert den Client
	 * @param yellowPages Wird benötigt, um die IP-Adressen der Services (speziell des GameServices) zu erhalten
	 * @param user Der angemeldete User.
	 * @throws IOException
	 * @throws UnirestException
	 * @throws Exception
	 */
	public RestopolyClient(YellowPagesService yellowPages, User user) throws IOException, UnirestException, Exception {
		try {
			BASE_URL = yellowPages.getBaseIP();
			gameService = yellowPages.getService(ServiceNames.GAME);
			boardService = yellowPages.getService(ServiceNames.BOARD);
			gson = new Gson();
			this.user = user;
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Laedt die aktuellen Spiele
	 * 
	 * @return Eine Liste mit Game-Informationen.
	 * @throws IOException
	 * @throws UnirestException
	 */
	public List<GameResponse> getGames() throws IOException, UnirestException {
		System.out.println("\n************* Get Games *************");
		List<GameResponse> data = new ArrayList<>();
		String uri = gameService.getUri();
		System.out.println(uri);
		JsonObject gameListResponse = get(uri);
		JsonArray gamesList = gameListResponse.getAsJsonArray("games");

		for (int i = 0; i < gamesList.size(); i++) {
			GameResponse game = gson.fromJson(gamesList.get(i), GameResponse.class);
			System.out.println(gamesList.get(i));
			
			game.setNumberOfPlayers(5);
			data.add(game);
		}
		return data;
	}

	/**
	 * Liefert alle beim Game angemeldeten Spieler
	 * @param gameID Die ID des Games
	 * @return Eine Liste mit Informationen über alle Spieler. 
	 * @throws IOException
	 * @throws UnirestException
	 * @throws Exception
	 */
	public List<PlayerInformation> getPlayers(String gameID) throws IOException, UnirestException, Exception {
		System.out.println("\n**************  Get Players **************");
		List<PlayerInformation> data = new ArrayList<>();

		String gameServiceUri = gameService.getUri();
		String gamesPlayersUri = gameServiceUri + "/" + gameID + "/players";
		JsonObject playerListResponse = get(gamesPlayersUri);

		System.out.println(gamesPlayersUri);
		System.out.println("\n" + playerListResponse.toString());

		PlayerList playerList = gson.fromJson(playerListResponse.toString(), PlayerList.class);
		for (PlayerID playerID : playerList.getPlayers()) {

			// {"ready":false,"id":"/games/100/players/wario","user":"/user/wario"}
			JsonObject playerRessource = get(BASE_URL + playerID.getId());
			PlayerResponse player = gson.fromJson(playerRessource, PlayerResponse.class);
			System.out.println("PlayerResponse: " + gson.toJson(player));

			PlayerInformation playerInformation = new PlayerInformation();

			if (checkNotNull(player.getPawn())) {
				JsonObject pawnResponse = get(player.getPawn());
				Pawn pawnObject = gson.fromJson(pawnResponse, Pawn.class);
				playerInformation.setPawn(pawnObject.getId());
			}
			if (checkNotNull(player.getAccount())) {
				System.out.println(player.getAccount());
				JsonObject accountResponse = get(player.getAccount());
				Account accountObject = gson.fromJson(accountResponse, Account.class);
				playerInformation.setAccount(accountObject.getSaldo() + "");
			}
			if (checkNotNull(player.getReady())) {
				System.out.println("Ready: " + player.getReady());
//				JsonObject readyResponse = get(player.getReady());
//				System.out.println(readyResponse.toString());
//				playerInformation.setReady(player.getReady());
			}


			data.add(playerInformation);
		}
		return data;
	}

	/**
	 * Liefert alle Places des Spielfeldes.
	 * @param gameID Die ID des Games
	 * @return Eine Liste aller Places.
	 * @throws IOException
	 * @throws UnirestException
	 */
	public List<Place> getPlaces(String gameID) throws IOException, UnirestException {
		System.out.println("\n**************  Get Fields **************");
		List<Place> data = new ArrayList<>();


		String boardServiceUri = boardService.getUri();
		// String boardsPlacesUri = boardServiceUri + "/" + gameID + "/places";
		System.out.println(boardServiceUri);
		Board board = gson.fromJson(get(boardServiceUri + "/" + gameID), Board.class);
		System.out.println("Board: " + gson.toJson(board));

		List<Field> fields = board.getFields();
		for (Field field : fields) {
			System.out.println(gson.toJson(field));
			String placeUri = field.getPlace();
			JsonObject fieldRessource = get(BASE_URL + placeUri);
			// System.out.println("Antwort auf " + BASE_URL+placeUri + ":\n"
			// +fieldRessource.toString());
			String id = placeUri;
			// String name = "";
			// String owner = "";
			// String value = "";
			// String rent = "";
			// String cost = "";
			// String houses = "";
			// String hypocredit = "";
			// List<Player> players = null;
			Place place = gson.fromJson(fieldRessource.toString(), Place.class);
			place.setPlayers(field.getPawns());
			place.setID(id);
			data.add(place);
		}
		return data;
	}

	/**
	 * Liefert eine Liste aller SPielfiguren.
	 * @param gameID Die ID des games
	 * @return Liste aller Spielfiguren
	 * @throws UnirestException
	 * @throws IOException
	 */
	public List<Pawn> getPawns(String gameID) throws UnirestException, IOException {
		System.out.println("\n**************  Get Pawns **************");
		List<Pawn> data = new ArrayList<>();
		String boardServiceUri = boardService.getUri();
		String boardsPawnsUri = boardServiceUri + "/" + gameID + "/pawns";
		JsonObject json = get(boardsPawnsUri);
		PawnList pawnList = gson.fromJson(json, PawnList.class);
		for (String pawnUri : pawnList.getPawns()) {
			System.out.println(pawnUri);
		}
		// TODO
		return data;
	}
	
	/**
	 * Liefert die URI der Spielfigur des Users
	 * @param gameID Die ID des Games
	 * @return Die Uri des Pawn-Objektes des Users
	 * @throws UnirestException
	 * @throws IOException
	 */
	public String getPawnID(String gameID) throws UnirestException, IOException{
		System.out.println("\n************** Get PawnID **************");
		List<Pawn> data = getPawns(gameID);
		String result = "";
		for(Pawn pawn : data){
			if(pawn.getPlayer().equals(user.getUri())){
				result = pawn.getPlayer();
			}
		}		
		return result;
	}

	/**
	 * Wuerfelt fuer den angemeldeten User und liefert das Ergebnis zurueck.
	 * Außerdem wird die Spielfigur verschoben.
	 * 
	 * @return Das Wurfergebnis
	 * @throws IOException
	 * @throws UnirestException 
	 */
	public int rollDice(String gameID) throws IOException, UnirestException {
		System.out.println("\n**************  Roll Dice  **************");

		String boardServiceUri = boardService.getUri();
		String pawnID = getPawnID(gameID);
		String boardsDiceRollUri = boardServiceUri + "/" + gameID + "/pawns" + pawnID + "/roll";
		
		Unirest.post(boardsDiceRollUri);
		
		JsonObject diceRolls = get(boardsDiceRollUri);
		DiceRolls rolls = gson.fromJson(diceRolls, DiceRolls.class);	
//		Board board = gson.fromJson(get(boardServiceUri + "/" + gameID), Board.class);
		Dice lastThrown = rolls.getRolls().get(rolls.getRolls().size()-1);
		return lastThrown.getNumber();// dice.getNumber();
	}

	/**
	 * Erstellt ein neues Spiel
	 * @param gameName Der Name des Spiels
	 * @throws IOException
	 * @throws UnirestException
	 */
	public void createANewGame(String gameName) throws IOException, UnirestException {
		System.out.println("\n************* Create New Game *************");
		String uri = gameService.getUri();
		CreateGame game = new CreateGame(gameName);
		System.out.println(uri);
		System.out.println(game);
		postData(game, uri);
	}

	/**
	 * Meldet des User als Spieler bei einem Game an.
	 * @param gameID Die ID des Games.
	 * @throws IOException
	 * @throws UnirestException
	 */
	public void enterGame(String gameID) throws IOException, UnirestException {
		System.out.println("\n************* Enter Game *************");
		String uri = gameService.getUri();
		String gamesPlayersUri = uri + "/" + gameID + "/players";

		System.out.println(gamesPlayersUri);
		postData(user, gamesPlayersUri);
		System.out.println("Der User " + gson.toJson(user) + " betritt das Spiel.");
	}

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
	public JsonObject postData(Object object, String serviceUri) throws IOException, UnirestException {
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
	public JsonObject get(String uri) throws IOException, UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get(uri).header("accept", "application/json").asJson();

		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		return responseObject;
	}

	/**
	 * Hilfsmethode: Prueft, ob das uebergebene Objekt not null ist.
	 * @param object
	 * @return
	 */
	private boolean checkNotNull(Object object) {
		if (object == null) {
			return false;
		}
		return true;
	}

	/**
	 * Liefert den angemeldeten User zurueck.
	 * @return
	 */
	public User getUser() {
		return user;
	}
}
