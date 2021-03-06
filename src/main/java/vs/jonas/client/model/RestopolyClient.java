package vs.jonas.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.ServiceUnavailableException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jonas.client.json.Account;
import vs.jonas.client.json.Board;
import vs.jonas.client.json.BrokerPlace;
import vs.jonas.client.json.CreateGame;
import vs.jonas.client.json.DiceRolls;
import vs.jonas.client.json.Field;
import vs.jonas.client.json.GameResponse;
import vs.jonas.client.json.Pawn;
import vs.jonas.client.json.Place;
import vs.jonas.client.json.PlayerID;
import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.json.PlayerList;
import vs.jonas.client.json.PlayerResponse;
import vs.jonas.client.json.User;
import vs.jonas.client.model.comparator.PlaceComparator;
import vs.jonas.exceptions.EstateAlreadyOwnedException;
import vs.jonas.exceptions.NotExpectedStatusCodeException;
import vs.jonas.exceptions.PlayerDoesNotHaveTheMutexException;
import vs.jonas.exceptions.PlayerHasAlreadyRolledTheDiceException;
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
	private JSONService brokerService;
	private final String SLASH = "/";
	private final String SLASH_PLAYERS = "/players";
	private final String SLASH_PAWNS = "/pawns";
	private final String SLASH_ROLL = "/roll";
	private final String SLASH_READY = "/ready";
	private final String SLASH_TURN = "/turn";
	private final String SLASH_STATUS = "/status";
	private final String SLASH_OWNER = "/owner";
	private final String SLASH_HYPOTHECARYCREDIT = "/hypothecarycredit ";
	private final String SLASH_TRADE = "/trade";
	private Gson gson;

	/**
	 * Initialisiert den Client
	 * @param yellowPages Wird benï¿½tigt, um die IP-Adressen der Services (speziell des GameServices) zu erhalten
	 * @param user Der angemeldete User.
	 * @throws UnirestException
	 */
	public RestopolyClient(YellowPagesService yellowPages) throws UnirestException{
		try {
			gameService = yellowPages.getService(ServiceNames.GAME);
			boardService = yellowPages.getService(ServiceNames.BOARD);
			brokerService = yellowPages.getService(ServiceNames.BROKER);
			gson = new Gson();
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}
	}

	/* ************************************ GameServices ***************************************** */
	
	/**
	 * Erstellt ein neues Spiel
	 * @param gameName Der Name des Spiels
	 * @throws UnirestException
	 * @throws NotExpectedStatusCodeException 
	 */
	public void createANewGame(String gameName) throws UnirestException, NotExpectedStatusCodeException {
		System.out.println("\n************* Create New Game *************");
		String uri = gameService.getUri();
		CreateGame game = new CreateGame(gameName);
		System.out.println(uri);
		System.out.println(game);
		postData(game, uri);
	}

	/**
	 * Laedt die aktuellen Spiele
	 * 
	 * @return Eine Liste mit Game-Informationen.
	 * @throws UnirestException 
	 */
	public List<GameResponse> getGames() throws UnirestException {
		System.out.println("\n************* Get Games *************");
		List<GameResponse> data = new ArrayList<>();
		String uri = gameService.getUri();
		System.out.println(uri);
		JsonObject gameListResponse = get(uri);
		JsonArray gamesList = gameListResponse.getAsJsonArray("games");
		
		for (int i = 0; i < gamesList.size(); i++) {
			GameResponse game = gson.fromJson(gamesList.get(i), GameResponse.class);
			System.out.println(gamesList.get(i));
			int numberOfPlayers = getPlayers(game.getName()).size();
			game.setNumberOfPlayers(numberOfPlayers);
			data.add(game);
		}
		return data;
	}
	
	/**
	 * Meldet den User als Spieler bei einem Game an.
	 * @param gameID Die ID des Games.
	 * @throws UnirestException
	 * @throws NotExpectedStatusCodeException 
	 */
	public void enterGame(String gameID, User user) throws UnirestException, NotExpectedStatusCodeException {
		System.out.println("\n************* Enter Game *************");
		String uri = gameService.getUri();
		String gamesPlayersUri = uri + SLASH + gameID + SLASH_PLAYERS;
		
		System.out.println(gamesPlayersUri);
		JsonObject response = postData(user, gamesPlayersUri);
		System.out.println("Der User " + gson.toJson(user) + " betritt das Spiel.");
		System.out.println("Received: " + response);
		user.setPlayerUri("/games" + SLASH + gameID + SLASH_PLAYERS+"/"+user.getName());
	}
	
	/**
	 * Wird aufgerufen, wenn ein Spieler bereit ist das Spiel zu beginnen und
	 * wenn er mit seinem Spielzug fertig ist.
	 * @param gameID
	 * @param user
	 * @throws UnirestException 
	 */
	public void setReady(String gameID, User user) throws UnirestException{
		String readyUri = gameService.getUri() + SLASH + gameID + SLASH_PLAYERS + SLASH + user.getName() + SLASH_READY;
		System.out.println(readyUri);
		Unirest.put(readyUri).asString();
	}

	/**
	 * Startet das Spiel
	 * @param gameID
	 * @throws UnirestException
	 */
	public void startGame(String gameID) throws UnirestException {
		System.out.println("\n************* Start Game *************");
		HttpResponse<String> res = Unirest.put(gameService.getUri() + SLASH + gameID + SLASH_STATUS).asString();
		System.out.println(res.getStatus());
	}
	/* ************************************ PlayerServices ***************************************** */
	

	/**
	 * Prueft ob alle Spieler bereit sind. 
	 * @param gameID
	 * @return
	 * @throws UnirestException
	 */
	public boolean allPlayersReady(String gameID) throws UnirestException{
		boolean bool = true;
		List<PlayerInformation> players = getPlayers(gameID);
		for(PlayerInformation player : players){
			if(!player.isReady()){
				bool = false;
				break;
			}
		}
		return bool;
	}

	/**
	 * Liefert alle beim Game angemeldeten Spieler
	 * @param gameID Die ID des Games
	 * @return Eine Liste mit Informationen über alle Spieler. 
	 * @throws UnirestException
	 */
	public List<PlayerInformation> getPlayers(String gameID) throws UnirestException {
//		System.out.println("\n**************  Get Players **************");
		List<PlayerInformation> data = new ArrayList<>();

		String gameServiceUri = gameService.getUri();
		String gamesPlayersUri = gameServiceUri + SLASH + gameID + SLASH_PLAYERS;
		JsonObject playerListResponse = get(gamesPlayersUri);

//		System.out.println(gamesPlayersUri);
//		System.out.println("\n" + playerListResponse.toString());
		
		PlayerResponse playerWithMutex = getPlayerWithMutex(gameID);

		PlayerList playerList = gson.fromJson(playerListResponse.toString(), PlayerList.class);
		for (PlayerID playerID : playerList.getPlayers()) {
			String newUri = gameServiceUri.replaceAll("/games", "");
//			System.out.println("Request an:" + newUri + playerID.getId());
			JsonObject playerRessource = get(newUri + playerID.getId());
			
			PlayerResponse player = gson.fromJson(playerRessource, PlayerResponse.class);
			PlayerInformation playerInformation = new PlayerInformation();

			if (checkNotNull(player.getPawn())) {
				String boardServiceUri = boardService.getUri();
				
				JsonObject pawnResponse = get(boardServiceUri.replaceAll("/boards", "") + player.getPawn());
				Pawn pawnObject = gson.fromJson(pawnResponse, Pawn.class);
				playerInformation.setPawn(pawnObject.getId());
			}
			if (checkNotNull(player.getAccount())) {
				JsonObject accountResponse = get(player.getAccount());
				Account accountObject = gson.fromJson(accountResponse, Account.class);
				playerInformation.setAccount(accountObject.getSaldo() + "");
			}
			if (checkNotNull(player.getReady())) {
				HttpResponse<String> ready = Unirest.get(player.getReady()).asString();
				if(ready.getBody().equals("true")){
					playerInformation.setReady(true);
				}
			} 
			if(checkNotNull(player.getId())){
				playerInformation.setUri(player.getId());
			}
			
			if(player.equals(playerWithMutex)){
				playerInformation.setHasTurn(true);
			}
			data.add(playerInformation);
		}
		return data;
	}
	
	
	/**
	 * Liefert den Spieler zurück der gerade den Mutex hält
	 * @param gameID
	 * @return
	 * @throws UnirestException
	 */
	public PlayerResponse getPlayerWithMutex(String gameID) throws UnirestException{
		String playerUri = gameService.getUri() + SLASH + gameID + "/player" + SLASH_TURN;
		JsonObject playerRessource = get(playerUri);
		return gson.fromJson(playerRessource, PlayerResponse.class);
	}
	
	/* ************************************ BrokerServices ***************************************** */
	
	/**
	 * Kauft ein Grundstueck oder ein Haus für einen Spieler zu einem bestimmten Spiel
	 * @param gameID
	 * @param place
	 * @param user
	 * @throws UnirestException
	 * @throws EstateAlreadyOwnedException
	 */
	public void buyEstate(String gameID, Place place, User user) throws UnirestException, EstateAlreadyOwnedException {
		System.out.println("\n ************* Buy Estate ***************");
		System.out.println(user);
		
		String brokerPlaceUri = place.getBroker();
		
		System.out.println("BrokerUri: " + brokerPlaceUri);		
		String brokerServiceUri = brokerService.getUri();
		String brokerPlaceOwnerUri = brokerServiceUri.replaceAll("/broker", "")+brokerPlaceUri+SLASH_OWNER;
		System.out.println("Uri to Owner Endpoint:" + brokerPlaceOwnerUri);
		System.out.println("Body: " +user.getPlayerUri());

//		JsonObject response = postData(thisPlayer.getId(), brokerPlaceOwnerUri);
		HttpResponse<String> response = Unirest.post(brokerPlaceOwnerUri).body(gson.toJson(user.getPlayerUri())).asString();
		System.out.println("Response: "+response + " Status: " + response.getStatus());
		if(response.getStatus() != 200){
			throw new EstateAlreadyOwnedException();
		}
	}
	
	
	/**
	 * Liefert ein Place-Objekt zu einer
	 * @param gameID 
	 * @param placeID Die ID des Places, z.B. /100/places/1
	 * @return
	 * @throws UnirestException
	 */
	public Place getPlace(String placeID) throws UnirestException{
		String boardServiceUri = boardService.getUri();
		String brokerServiceUri = brokerService.getUri();
		JsonObject fieldRessource = get(boardServiceUri + placeID);
		Place place = gson.fromJson(fieldRessource.toString(), Place.class);
		
		String brokerPlaceUri = brokerServiceUri.replace("/broker", "")+place.getBroker();
		JsonObject brokerPlaceResponse = get(brokerPlaceUri);
		BrokerPlace brokerPlace = gson.fromJson(brokerPlaceResponse, BrokerPlace.class);
		
		// Kosten = je nach Anzahl der Häuser unterschiedlich
		int numberOfHouses = brokerPlace.getHouses();
		if(numberOfHouses != -1){
			place.setRent(brokerPlace.getRent().get(numberOfHouses));
			if(numberOfHouses < 5){
				place.setCost(brokerPlace.getCost().get(numberOfHouses));
			} 
		} 
		place.setValue(brokerPlace.getValue());
		place.setHouses(brokerPlace.getHouses());
		String owner = getOwner(brokerPlace.getOwner());
		place.setOwner(owner);
		return place;
	}
	
	/**
	 * Liefert die ID des Owners
	 * @param ownerUri
	 * @return
	 * @throws UnirestException 
	 */
	private String getOwner(String ownerUri) {
		String ownerID = "";
		if(ownerUri != null && !ownerUri.equals("")){
			String brokerServiceUri = brokerService.getUri();
			String brokerServiceOwnerUri = brokerServiceUri.replaceAll("/broker", "") + ownerUri;
			try {
				JsonObject json = get(brokerServiceOwnerUri);
				PlayerResponse response = gson.fromJson(json, PlayerResponse.class);
				ownerID = response.getId();
			} catch (UnirestException e) {
//				es kann null zurückkommen im body und das kann nicht geparsed werden.
//				das bedeutet nur, dass das Grundstueck niemandem zugewiesen wurde, 
//				muss also nicht behandelt werden.
			}
		}
		return ownerID;
	}

	/**
	 * Liefert alle Places für einen bestimmten Spieler
	 * @param gameID
	 * @param pawnID
	 * @return
	 * @throws UnirestException
	 */
	public List<Place> getPlacesFor(String gameID, String pawnID) throws UnirestException{
		System.out.println("*** Fetch Places For " + pawnID + " ****");
		List<Place> places = new ArrayList<>();
		
		for(Place place : getPlaces(gameID)){
			Place placeWithWholeInformation = getPlace(place.getID());
			if(placeWithWholeInformation.getOwner() != null && !placeWithWholeInformation.getOwner().equals("")){
				
//				Vorher
//				String brokerServiceUri = brokerService.getUri();
//				String brokerPlaceOwnerUri = brokerServiceUri.replaceAll("/broker", "")+placeWithWholeInformation.getOwner();
				
				// Nachher
				String brokerServiceUri = gameService.getUri();
				String brokerPlaceOwnerUri = brokerServiceUri.replaceAll("/games", "")+placeWithWholeInformation.getOwner();
				System.out.println("BROKER OWNER URI: " + brokerPlaceOwnerUri);
				try {
					JsonObject ownerResponse = get(brokerPlaceOwnerUri);
					PlayerResponse response = gson.fromJson(ownerResponse, PlayerResponse.class);
					if(response.getPawn().equals(pawnID)){
						System.out.println("added Place: " + placeWithWholeInformation);
						places.add(placeWithWholeInformation);
					}
				} catch (UnirestException e) {
					e.printStackTrace();
				}
			}
		}
		
		return places;
	}
	
	/**
	 * Versendet einen Trade Request
	 * @param placeBrokerUri /broker/:gameID/places/:placeID
	 * @param pawnName Der Name
	 * @throws UnirestException
	 */
	public void sendTradeRequest(String placeBrokerUri, String pawnName) throws UnirestException{
//		 /broker/<gameid>/places/<placeid>/trade/<pawnid>
		System.out.println("\n*********** Send Trade Request *************");
		String brokerUri = brokerService.getUri();
//		String tradeRequestUri = brokerUri + SLASH + gameID + "/places" + SLASH + placeName + "/trade" + SLASH + pawnName;
		String tradeRequestUri = brokerUri.replaceAll("/broker", "") + placeBrokerUri + SLASH_TRADE + SLASH + pawnName;
		System.out.println("Senden an: " + tradeRequestUri);
		HttpResponse<String> response = Unirest.post(tradeRequestUri).asString();
		System.out.println("Received: " + gson.fromJson(response.getBody(), String.class) + "  "+response.getStatus());
	}
	
	/**
	 * Takes the HypothecaryCredit
	 * @param placeBrokerUri
	 * @throws UnirestException
	 */
	public void takeHypothecaryCredit(String placeBrokerUri, String playerUri) throws UnirestException {
		System.out.println("Take HypothecaryCredit...");
		String brokerUri = brokerService.getUri();
		String tradeRequestUri = brokerUri.replaceAll("/broker", "") + placeBrokerUri + SLASH_HYPOTHECARYCREDIT;
		
		HttpResponse<String> response = Unirest.put(tradeRequestUri).body(gson.toJson(playerUri)).asString();
		System.out.println("Received Response: " + response.getBody() + " " + response.getStatus());
	}
	
	/* ************************************ BoardServices ***************************************** */
	
	/**
	 * Liefert alle Places des Spielfeldes.
	 * @param gameID Die ID des Games
	 * @return Eine Liste aller Places.
	 * @throws UnirestException
	 */
	public List<Place> getPlaces(String gameID) throws UnirestException {
//		System.out.println("\n**************  Get Fields **************");
		List<Place> data = new ArrayList<>();

		String boardServiceUri = boardService.getUri();
		System.out.println(boardServiceUri);
		Board board = gson.fromJson(get(boardServiceUri + SLASH + gameID), Board.class);
//		System.out.println("Board: " + gson.toJson(board));
		List<Field> fields = board.getFields();
		for (Field field : fields) {
			// http://...:.../boards/{gameID}/places/{placeID}
			//							-> liefert den Namen des Feldes und die Uri des Feldes beim Broker
			String placeUri = field.getPlace().replaceAll("/boards","");
			JsonObject fieldRessource = get(boardServiceUri + placeUri);
			Place place = gson.fromJson(fieldRessource.toString(), Place.class);
			place.setPlayers(field.getPawns());
			place.setID(placeUri);
			data.add(place);
		}
		return data;
	}
	
	/**
	 * Liefert das Place-Objekt auf dem sich der Spieler gerade befindet
	 * @param gameID
	 * @param playerName
	 * @return
	 * @throws JsonSyntaxException
	 * @throws UnirestException
	 */
	public Place getCurrentPlace(String gameID, String playerName) throws JsonSyntaxException, UnirestException{
		String boardsServiceUri = boardService.getUri();
		String boardsPlaceUri = boardsServiceUri.replaceAll("/boards", "") + getPlaceUri(gameID, playerName);
		System.out.println("Received Place URI: " + boardsPlaceUri);
		
		Place place = gson.fromJson(get(boardsPlaceUri),Place.class);
		return place;
	}
	
	/**
	 * Liefert ein Player-Objekt, das allumfassende Informationen besitzt und nicht nur Uris.
	 * @param gameID
	 * @param player
	 * @return
	 * @throws UnirestException
	 */
	public Player getPlayerWithWholeInformation(String gameID, PlayerInformation player) throws UnirestException {
		Player result = new Player();
		List<Place> places = getPlacesFor(gameID, player.getPawn());
		System.out.println("PlayerPawnUri: " + player.getPawn());
		double averageDiceRoll = getAverageDiceRollRateFor(gameID,player.getPawn());
		int placesValue = 0;
		int placesRent = 0;
		int placesCost = 0;
		int placesHouses = 0;
		for(Place place : places){
			placesValue+= place.getValue();
			placesRent += place.getRent();
			placesCost += place.getCost();
			placesHouses += place.getHouses();
		}		
		Collections.sort(places, new PlaceComparator());
		result.setUri(player.getUri());
		result.setName(player.getPawn());
		result.setAccount(player.getAccount());
		result.setPlaces(places);
		result.setAverageDiceRoll(averageDiceRoll);
		if(places.size()>0){
			result.setAveragePlaceValue(placesValue/places.size());
			result.setAverageRentValue(placesRent/places.size());
			result.setAveragePlaceCostValue(placesCost/places.size());
			result.setAverageHouses(placesHouses/places.size());
		}
		return result;
	}
	
	public Player getPlayer(String gameID, String playerID) throws UnirestException{
		String gameServiceUri = gameService.getUri();
		String newUri = gameServiceUri.replaceAll("/games", "");
		JsonObject playerRessource = get(newUri + playerID);
		
		PlayerResponse player = gson.fromJson(playerRessource, PlayerResponse.class);
		System.out.println("PLAYER RESPONSE: " + player.toString());
		PlayerInformation playerInformation = new PlayerInformation();
		if (checkNotNull(player.getPawn())) {
			String boardServiceUri = boardService.getUri();
			
			JsonObject pawnResponse = get(boardServiceUri.replaceAll("/boards", "") + player.getPawn());
			Pawn pawnObject = gson.fromJson(pawnResponse, Pawn.class);
			playerInformation.setPawn(pawnObject.getId());
		}
		if (checkNotNull(player.getAccount())) {
			JsonObject accountResponse = get(player.getAccount());
			Account accountObject = gson.fromJson(accountResponse, Account.class);
			playerInformation.setAccount(accountObject.getSaldo() + "");
		}
		if (checkNotNull(player.getReady())) {
			HttpResponse<String> ready = Unirest.get(player.getReady()).asString();
			if(ready.getBody().equals("true")){
				playerInformation.setReady(true);
			}
		} 
		if(checkNotNull(player.getId())){
			playerInformation.setUri(player.getId());
		}		
		return getPlayerWithWholeInformation(gameID, playerInformation);
	}

	/**
	 * Liefert den Durchschnitt aller Wurfergebnisse eines Spielers
	 * @param gameID
	 * @param boardsPawnUri
	 * @return
	 * @throws UnirestException
	 */
	private double getAverageDiceRollRateFor(String gameID, String boardsPawnUri) throws UnirestException {
		
		//SLASH + gameID + SLASH_PAWNS + SLASH + user.getName() + SLASH_ROLL
		String boardServiceUri = boardService.getUri();
		String uri = boardServiceUri.replaceAll("/boards", "")+ boardsPawnUri + SLASH_ROLL;
		JsonObject diceRolls = get(uri);
		DiceRolls rolls = gson.fromJson(diceRolls, DiceRolls.class);
		System.out.println(rolls);
		
		int averageRate = 0;
		for(Dice dice : rolls.getRolls()){
			averageRate+=dice.getNumber();
		}		
		if(rolls.getRolls().size()>0){
			return averageRate/rolls.getRolls().size();
		} else return 0;
	}

	/**
	 * Liefert die PlaceURI der Spielfigur des Users
	 * @param gameID Die ID des Games
	 * @return Die PlaceUri des Pawn-Objektes des Users
	 * @throws UnirestException
	 */
	public String getPlaceUri(String gameID, String playerName) throws UnirestException{
		String boardServiceUri = boardService.getUri();
		String boardsPawnsUri = boardServiceUri + SLASH + gameID + SLASH_PAWNS + SLASH + playerName;
		System.out.println("BoardsPawnsUri: " + boardsPawnsUri);
		JsonObject json = get(boardsPawnsUri);
		Pawn playerPawn = gson.fromJson(json, Pawn.class);
		return playerPawn.getPlace();
	}

	/**
	 * Wuerfelt fuer den angemeldeten User und liefert das Ergebnis zurueck.
	 * Außerdem wird die Spielfigur verschoben.
	 * 
	 * @return Das Wurfergebnis
	 * @throws UnirestException 
	 * @throws PlayerHasAlreadyRolledTheDiceException 
	 * @throws PlayerDoesNotHaveTheMutexException 
	 */
	public int rollDice(String gameID, User user) throws UnirestException, PlayerHasAlreadyRolledTheDiceException, PlayerDoesNotHaveTheMutexException {
		System.out.println("\n**************  Roll Dice  **************");

		String boardServiceUri = boardService.getUri();
		String boardsDiceRollUri = boardServiceUri + SLASH + gameID + SLASH_PAWNS + SLASH + user.getName() + SLASH_ROLL;
		
		System.out.println(boardsDiceRollUri);
		
		HttpResponse<String> result = Unirest.post(boardsDiceRollUri).asString();
		System.out.println(result.getBody());
		switch(result.getStatus()){
		case 409: throw new PlayerHasAlreadyRolledTheDiceException(user.getName() + " hat bereits gewuerfelt."); 
		case 400: throw new PlayerDoesNotHaveTheMutexException(user.getName() + " hat den Mutex nicht.");
		}

		Dice lastThrown = getLastDiceRoll(SLASH + gameID + SLASH_PAWNS + SLASH + user.getName() + SLASH_ROLL);
		return lastThrown.getNumber();
	}
	
	/**
	 * Liefert den letzten Wurf des Spielers
	 * @param boardsDiceRollUri
	 * @return
	 * @throws UnirestException
	 */
	public Dice getLastDiceRoll(String boardsDiceRollUri) throws UnirestException{
		String boardServiceUri = boardService.getUri();
		String uri = boardServiceUri + boardsDiceRollUri;
		System.out.println("GetLastDiceRollUri: " + uri);
		JsonObject diceRolls = get(uri);
		DiceRolls rolls = gson.fromJson(diceRolls, DiceRolls.class);	
		Dice lastThrown = rolls.getRolls().get(rolls.getRolls().size()-1);
		return lastThrown;
	}
	
	// *************************************** Hilfsmethoden *******************************************
	
	/**
	 * Sendet ein Objekt an einen Service
	 * 
	 * @param object
	 *            Das Objekt, dass eingetragen werden soll.
	 * @param serviceUri
	 * @return Den Statuscode der Anfrage.
	 * @throws UnirestException
	 * @throws NotExpectedStatusCodeException 
	 */
	public JsonObject postData(Object object, String serviceUri) throws UnirestException, NotExpectedStatusCodeException {
		HttpResponse<JsonNode> jsonResponse = Unirest.post(serviceUri).header("accept", "application/json")
				.body(gson.toJson(object)).asJson();
		int status = jsonResponse.getStatus();
		System.err.println("Received Statuscode: " + status);
		if(status != 200 && status != 201){
			throw new NotExpectedStatusCodeException("Invalid Response: " + jsonResponse.getStatus()+ " " +jsonResponse.getStatusText());
		}
		JsonObject responseObject = gson.fromJson(String.valueOf(jsonResponse.getBody()), JsonObject.class);
		return responseObject;
	}

	/**
	 * Schickt eine GET Abfrage an einen Service
	 * 
	 * @param uri
	 *            Die URI vom Service
	 * @return
	 * @throws UnirestException
	 */
	public JsonObject get(String uri) throws UnirestException {
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

}
