package vs.jan.services.run.boardservice;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.exception.InvalidInputException;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONField;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPawnList;
import vs.jan.json.boardservice.JSONPlace;
import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jan.model.boardservice.Pawn;
import vs.jan.tools.HttpService;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.malte.example.json.CreateGameExDTO;
import vs.malte.models.Player;
import vs.malte.services.GamesService;
import vs.malte.services.GamesServiceAPI;

public class RunBoardExample {

	private static final Gson GSON = new Gson();
	private static final int TIMEOUT = 500;
	@SuppressWarnings("unused")
	private static BoardRESTApi boardApi;

	@SuppressWarnings("unused")
	private static DiceService diceApi;

	/**
	 * Startet den Boardservice und fuehrt ein paar Testoperationen aus.
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws InterruptedException, MalformedURLException {

		//BankService.run();
		new GamesServiceAPI();
		Map<String, JSONService> neededServicesDice = getNeededServices(ServiceNames.DICE);
		new EventService().startService(); // Der EventService muss f�r den
																				// DiceService laufen
		new DiceService(neededServicesDice).startService();
		new UserServiceRESTApi();

		boardApi = new BoardRESTApi();
		
		setupGame();

	}

	private static void setupGame() throws InterruptedException, MalformedURLException {
		setupBoard();

	}

	private static void setupBoard() throws InterruptedException, MalformedURLException {
		String gameUri = "http://localhost:4567/games";
		int boardID = 42;

		createBoard(boardID, gameUri);
//		placeBoard(boardID);
//		checkBoardAdded(boardID);
		setupUser(boardID, gameUri);
		startGame(boardID);
		letCurrPlayerRollDice(boardID);
		getFinalBoardState(boardID);

	}

	private static void placeBoard(int boardID) {
		
		JSONBoard board = new JSONBoard("" + boardID);
		
		HttpService.put("http://localhost:4567/boards/" + boardID, board, 200);
		
	}

	private static void letCurrPlayerRollDice(int boardID) {
		
		for(int i = 0; i < 4; i++){
			System.out.println("Let Player with mutex roll the dice");
			System.out.println("-------------------------------------------------------------------------------------------");
			String json = HttpService.get("http://localhost:4567/games/" + boardID + "/player/current", 200);

			Player p = GSON.fromJson(json, Player.class);
			HttpService.put("http://localhost:4567/games/" + boardID + "/player/turn", p, 201);
			

			String pawnUri = p.getPawn();
			String [] u = pawnUri.split("/");
			String id = u[u.length - 1];
			String uri = "http://localhost:4567/boards/42/pawns/" + id;
			p.setPawn(uri);
			System.out.println("Get Pawn with uri: " + p.getPawn());
			String json2 = HttpService.get(p.getPawn(), 200);
			JSONPawn pawn = GSON.fromJson(json2, JSONPawn.class);
			System.out.println("Got Pawn: " + json2);
			System.out.println("Pawn rolls dice: " + json2);
			HttpService.post("http://localhost:4567" + pawn.getRoll(), null, 200);
			System.out.println();
			System.out.println("Player: " + json + " releases the Mutex");
			System.out.println("ID: " + id);
			HttpService.put("http://localhost:4567/games/" + boardID + "/players/" + id + "/ready", null, 200);
		}

		
	}

	private static void startGame(int boardID) {
		System.out.println("Start game: " + boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		HttpService.put("http://localhost:4567/games/" + boardID + "/status", null, 200);
		System.out.println("SUCCESS");
		System.out.println();
	}

	@SuppressWarnings("unused")
	private static void updateBoard(int boardID) {

		JSONBoard board = new JSONBoard("" + boardID);

		JSONField field = new JSONField("/boards/" + boardID + "/places/2");
		JSONPawn pawn = new JSONPawn("/game/" + boardID + "/players/mario");
		pawn.setId("/boards/" + boardID + "/pawns/mario");
		field.getPawns().add(pawn.getId());
		board.setFields(new ArrayList<>(Arrays.asList(field)));
		HttpService.put("http://localhost:4567/boards/" + boardID, board, 200);

	}

	private static void setupUser(int boardID, String gameUri) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();
		p1.setUserName("mario");
		p2.setUserName("wario");
		p3.setUserName("yoshi");
		p4.setUserName("donkeykong");
		String gamePlayerUri = gameUri + "/" + boardID + "/players";
		System.out.println("GameplayerURI: " + gamePlayerUri);
		System.out.println("Create Some Users on Game: " + boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		createUser(boardID, gamePlayerUri, p1);
		createUser(boardID, gamePlayerUri, p2);
		createUser(boardID, gamePlayerUri, p3);
		createUser(boardID, gamePlayerUri, p4);
		
		System.out.println("SUCCESS");
		System.out.println();
		
		System.out.println("Set Users ready on Game: " + boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		setUserReady(p1, boardID);
		setUserReady(p2, boardID);
		setUserReady(p3, boardID);
		setUserReady(p4, boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		checkPlayersAddedOnUserService();
		System.out.println("SUCCESS");
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}


	private static void setUserReady(Player p1, int boardID) throws InterruptedException {
		
		Thread.sleep(TIMEOUT);
		String json = HttpService.get("http://localhost:4567/games/" + boardID + "/players/" + p1.getUserName(), 200);
		System.out.println("User is ready: " + json);
		Player p = GSON.fromJson(json, Player.class);
		String [] u = p.getId().split("/");
		String id = u[u.length - 1];
		HttpService.put( "http://localhost:4567/games/" + boardID + "/players/" + id + "/ready", null, 200);
		System.out.println("SUCCESS");
		System.out.println();
	}

	private static void checkPlayersAddedOnUserService() {

		System.out.println("Check players added to Userservice...");
		String userServUri = "http://localhost:4567/users";
		System.out.println("Added Users: " + HttpService.get(userServUri, HttpURLConnection.HTTP_OK));

	}

	private static void createUser(int boardID, String gamePlayerUri, Player player) throws InterruptedException {
		Thread.sleep(TIMEOUT);
		HttpService.post(gamePlayerUri, player, HttpURLConnection.HTTP_CREATED);
		System.out.println("Created Player: " + GSON.toJson(player));
		System.out.println();
	}

	private static void getFinalBoardState(int boardID) throws InterruptedException {
		System.out.println("FINAL BOARD STATE: ");
		System.out.println("-------------------------------------------------------------------------------------------");
		checkBoardAdded(boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println("FINISHED");
		System.out.println();

	}


	private static JSONPlace getPlace(String placeUri) {

		try {

			System.out.println("Get Place with uri: " + placeUri + " ...");
			String json = HttpService.get("http://localhost:4567" + placeUri, HttpURLConnection.HTTP_OK);
			JSONPlace place = GSON.fromJson(json, JSONPlace.class);
			System.out.println("SUCCESS");
			System.out.println("Fetched Place: " + GSON.toJson(place));

			if (place != null) {
				return place;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new InvalidInputException();
	}

	private static List<String> getPlacesUris(int boardID) {

		System.out.println("Get Places Uris...");
		String json = HttpService.get("http://localhost:4567/boards/" + boardID + "/places", HttpURLConnection.HTTP_OK);

		@SuppressWarnings("unchecked")
		List<String> places = GSON.fromJson(json, List.class);

		if (places != null) {
			System.out.println("SUCCESS");
			System.out.println("fetched place uris: " + GSON.toJson(places));
			return places;
		}

		throw new InvalidInputException();
	}

	private static void deletePawns(int boardID) throws InterruptedException {
		System.out.println("Delete a random pawn on the board...");
		System.out.println("-------------------------------------------------------------------------------------------");
		JSONPawnList list = null;
		try {
			Thread.sleep(TIMEOUT);
			System.out.println("Old PawnList:");
			list = getPawnsOnBoard(boardID);
			System.out.println();
			System.out.println(GSON.toJson(list));

			List<JSONPawn> pawns = new ArrayList<>();

			for (String pawnUri : list.getPawns()) {
				JSONPawn p = getPawn(boardID, pawnUri);
				pawns.add(p);
			}

			// Get Random Pawn
			int index = (int) (Math.random() * pawns.size());
			JSONPawn pawn = pawns.get(index);
			deletePawn(pawn, boardID);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("New PawnList...");
		list = getPawnsOnBoard(boardID);
		System.out.println();
		System.out.println(GSON.toJson(list));
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

	private static void deletePawn(JSONPawn pawn, int boardID) {

		try {
			System.out.println("Pawn that will be deleted: " + GSON.toJson(pawn));
			HttpService.delete("http://localhost:4567" + pawn.getId(), HttpURLConnection.HTTP_OK);
			System.out.println("SUCCESS");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void updatePawns(int boardID) {
		System.out.println("Update Pawninformation of pawns on the board...");
		System.out.println("-------------------------------------------------------------------------------------------");

		try {
			Thread.sleep(TIMEOUT);
			JSONPawnList list = getPawnsOnBoard(boardID);
			List<JSONPawn> pawns = new ArrayList<>();

			for (String pawnUri : list.getPawns()) {
				JSONPawn p = getPawn(boardID, pawnUri);
				pawns.add(p);
			}

			// Get Random Pawn
			int index = (int) (Math.random() * pawns.size());
			JSONPawn pawn = pawns.get(index);
			updatePawn(pawn, boardID);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

	private static void updatePawn(JSONPawn pawn, int boardID) {

		System.out.println("Pawn that will be updated: " + GSON.toJson(pawn));
		System.out.println("Update PlayerUri...");
		pawn.setPlayer("/games/" + boardID + "/players/updated");
		HttpService.put("http://localhost:4567" + pawn.getId(), pawn, HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println();

	}

	private static void letPawnsRollDice(int boardID) throws InterruptedException {
		System.out.println("Ĺet Pawns roll the dice on the Board: " + boardID);
		System.out.println("-------------------------------------------------------------------------------------------");

		JSONPawnList list = getPawnsOnBoard(boardID);
		List<JSONPawn> pawns = new ArrayList<>();

		for (String pawnUri : list.getPawns()) {
			JSONPawn p = getPawn(boardID, pawnUri);
			pawns.add(p);
		}

		System.out.println("Old Pawns: " + GSON.toJson(pawns));
		System.out.println();

		for (JSONPawn p : pawns) {
			rollDice(p);
		}
		pawns = new ArrayList<>();
		for (String pawnUri : list.getPawns()) {
			JSONPawn p = getPawn(boardID, pawnUri);
			pawns.add(p);
		}
		System.out.println("Moved Pawns: " + GSON.toJson(pawns));
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

	private static void rollDice(JSONPawn p) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		String diceUri = "http://localhost:4567" + p.getRoll();
		System.out.println("Pawn with uri: " + p.getId() + " rolls the dice...");
		System.out.println(diceUri);
		HttpService.post(diceUri, null, HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println();

	}

	private static JSONPawn getPawn(int boardID, String pawnUri) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		System.out.println("Get Pawn from Board with URI " + pawnUri + "...");
		String json = HttpService.get("http://localhost:4567" + pawnUri, HttpURLConnection.HTTP_OK);
		JSONPawn pawn = GSON.fromJson(json, JSONPawn.class);

		if (pawn != null) {
			System.out.println("SUCCESS");
			System.out.println("Fetched Pawn: " + GSON.toJson(pawn));
			System.out.println();
			return pawn;
		}
		throw new InvalidInputException();

	}

	private static JSONPawnList getPawnsOnBoard(int boardID) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		System.out.println("Get Pawns from Board with id " + boardID + "...");
		String json = HttpService.get("http://localhost:4567/boards/" + boardID + "/pawns", HttpURLConnection.HTTP_OK);
		JSONPawnList list = GSON.fromJson(json, JSONPawnList.class);
		if (list != null) {
			System.out.println("SUCCESS");
			System.out.println("Fetched Pawnlist: " + GSON.toJson(list));
			System.out.println();
			return list;
		}
		throw new InvalidInputException();
	}

	private static void createPawn(int boardID, String playerName) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		Pawn p = new Pawn();
		p.setPlayerUri("/games/" + boardID + "/players/" + playerName);
		System.out.println("Create Pawn: " + GSON.toJson(p.convert()));
		HttpService.post("http://localhost:4567/boards/" + boardID + "/pawns", p.convert(), HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println();

	}

	private static void checkBoardAdded(int boardID) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		System.out.println("Get Board with id " + boardID + "...");
		String json = HttpService.get("http://localhost:4567/boards/" + boardID, HttpURLConnection.HTTP_OK);
		JSONBoard board = GSON.fromJson(json, JSONBoard.class);
		System.out.println("SUCCESS");
		System.out.println("Board: " + GSON.toJson(board));
		System.out.println();

	}

	private static void createBoard(int boardID, String gameUri) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		System.out.println("Create Game on: " + gameUri);
		CreateGameExDTO g = new CreateGameExDTO();
		g.setName("" + boardID);
		HttpService.post(gameUri, g, HttpURLConnection.HTTP_CREATED);
		System.out.println("SUCCESS");
		System.out.println();
		System.out.println("Added Game");
		checkGameAdded(boardID, gameUri);
		System.out.println();
		System.out.println("Added Board");


		System.out.println("-------------------------------------------------------------------------------------------");

	}

	private static void checkGameAdded(int boardID, String gameUri2) {

		String json = HttpService.get(gameUri2, HttpURLConnection.HTTP_OK);
		System.out.println(json);

	}

	private static Map<String, JSONService> getNeededServices(String type) {
		Map<String, JSONService> services = new HashMap<>();

		// services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere

		if (type.equals(ServiceNames.DICE) || type.equals(ServiceNames.BOARD)) {
			JSONService s = new JSONService("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
					"http://localhost:4567/events");

			services.put(ServiceNames.EVENT, s);
		}

		return services;
	}

}