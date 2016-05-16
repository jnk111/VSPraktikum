package vs.jan.services.run.boardservice;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.gerriet.service.BankService;
import vs.jan.exceptions.InvalidInputException;
import vs.jan.models.Board;
import vs.jan.models.Field;
import vs.jan.models.Pawn;
import vs.jan.models.Place;
import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jan.models.StatusCodes;
import vs.jan.models.User;
import vs.jan.models.json.JSONBoard;
import vs.jan.models.json.JSONGameURI;
import vs.jan.models.json.JSONPawn;
import vs.jan.models.json.JSONPawnList;
import vs.jan.models.json.JSONPlace;
import vs.jan.services.boardservice.BoardRESTApi;
import vs.jan.services.boardservice.GameServiceFixed;
import vs.jan.services.userservice.UserServiceRESTApi;
import vs.jan.tools.HttpService;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.malte.json.CreateUserDTO;
import vs.malte.models.Game;
import vs.malte.services.GamesService;
import vs.malte.services.Player;

public class RunBoardExample {

	private static final Gson GSON = new Gson();
	private static final int TIMEOUT = 1000;
	private static BoardRESTApi boardApi;
	private static GameServiceFixed gameApi;

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

		Map<String, Service> neededServicesDice = getNeededServices(ServiceNames.DICE);
		BankService.run();
		new EventService().startService(); // Der EventService muss f�r den
																				// DiceService laufen
		new DiceService(neededServicesDice).startService();
		new UserServiceRESTApi();
		boardApi = new BoardRESTApi();
		gameApi = new GameServiceFixed();
		setupGame();

	}

	private static void setupGame() throws InterruptedException, MalformedURLException {
		setupBoard();

	}

	private static void setupBoard() throws InterruptedException, MalformedURLException {
		String gameUri = "http://localhost:4567/games";
		int boardID = 42;

		createBoard(boardID, gameUri);
		setupUser(boardID, gameUri);
		placeBoard(boardID);
		createPawns(boardID);
		letPawnsRollDice(boardID);
		updatePawns(boardID);
		deletePawns(boardID);
		putPlaces(boardID);
		getFinalBoardState(boardID);

	}

	private static void setupUser(int boardID, String gameUri) throws InterruptedException {

		Thread.sleep(TIMEOUT);
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();
		p1.setUser("/users/mario");
		p2.setUser("/users/wario");
		p3.setUser("/users/yoshi");
		p4.setUser("/users/donkeykong");
		String gamePlayerUri = gameUri + "/" + boardID + "/players";
		System.out.println("Create Some Users on Game: " + boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		createUser(boardID, gamePlayerUri, p1);
		createUser(boardID, gamePlayerUri, p2);
		createUser(boardID, gamePlayerUri, p3);
		createUser(boardID, gamePlayerUri, p4);
		System.out.println("SUCCESS");
		System.out.println();
		checkPlayersAddedOnUserService();
		System.out.println("SUCCESS");
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();
		// System.out.println("Create A User on Userservice");
		//
		//
		// User user = new User("/users/mario", "Mario",
		// "http://somehost:4567/client/mario");
		// System.out.println("User that will be created: " + GSON.toJson(user));
		// HttpService.post("http://localhost:4567/users", user,
		// StatusCodes.CREATED);
		// System.out.println("SUCCESS");
		//
		// System.out.println("-------------------------------------------------------------------------------------------");
		// System.out.println();
		// System.out.println();

	}

	private static void checkPlayersAddedOnUserService() {
		
		System.out.println("Check players added to Userservice...");
		String userServUri = "http://localhost:4567/users";
		System.out.println("Added Users: " + HttpService.get(userServUri, HttpURLConnection.HTTP_OK));
		
	}

	private static void createUser(int boardID, String gamePlayerUri, Player player) throws InterruptedException {
		//Thread.sleep(TIMEOUT);
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

	private static void createPawns(int boardID) throws InterruptedException {
		System.out.println("Create some Pawns on the board");
		System.out.println("-------------------------------------------------------------------------------------------");
		createPawn(boardID, "mario");
		createPawn(boardID, "wario");
		createPawn(boardID, "yoshi");
		createPawn(boardID, "donkeykong");
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();
	}

	private static void putPlaces(int boardID) throws MalformedURLException, InterruptedException {
		System.out.println("Update place information of a random place");
		System.out.println("-------------------------------------------------------------------------------------------");

		Thread.sleep(TIMEOUT);
		List<String> places = getPlacesUris(boardID);
		int index = (int) (Math.random() * places.size());
		String pUri = places.get(index);
		JSONPlace place = getPlace(pUri);
		System.out.println("Place that will be updated: " + GSON.toJson(place));
		Place p = new Place(pUri, place.getName(), place.getBroker());
		p.setName("Los");
		p.setBrokerUri("/broker/places/" + p.getName().toLowerCase());
		HttpService.put("http://localhost:4567" + pUri, p.convert(), HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println("Updated Place: " + GSON.toJson(p.convert()));

		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
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
		System.out.println("PĹet Pawns roll the dice on the Board: " + boardID);
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
		p.setPlaceUri("/boards/" + boardID + "/places/0");
		System.out.println("Create Pawn: " + GSON.toJson(p.convert()));
		HttpService.post("http://localhost:4567/boards/" + boardID + "/pawns", p.convert(), HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println();

	}

	private static void placeBoard(int boardID) throws InterruptedException {

		System.out.println("Place the Board");
		System.out.println("-------------------------------------------------------------------------------------------");
		checkBoardAdded(boardID);

		Board b = boardApi.getBoardService().getBoard("" + boardID);

		for (int i = 0; i <= 10; i++) {
			Field f = new Field();
			Place p = new Place("/boards/42/places/" + i);
			f.setPlace(p);
			b.getFields().add(f);
		}

		Thread.sleep(TIMEOUT);

		System.out.println("Fill Board with Information...");
		HttpService.put("http://localhost:4567/boards/" + boardID, b.convert(), HttpURLConnection.HTTP_OK);
		System.out.println("SUCCESS");
		System.out.println("Placed Board: " + GSON.toJson(b.convert()));
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
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
		Game g = new Game();
		g.setName("" + boardID);
		HttpService.post(gameUri, gameApi.createGame(GSON.toJson(g)), HttpURLConnection.HTTP_CREATED);
		System.out.println("SUCCESS");
		System.out.println();
		System.out.println("Added Game");
		checkGameAdded(boardID, gameUri);
		System.out.println();
		System.out.println("Added Board");
		checkBoardAdded(boardID);

		System.out.println("-------------------------------------------------------------------------------------------");

		// System.out.println("Create Board");
		// System.out.println("-------------------------------------------------------------------------------------------");
		// JSONGameURI gameid = new JSONGameURI("/games/" + boardID);
		// HttpService.post("http://localhost:4567/boards", gameid,
		// HttpURLConnection.HTTP_OK);
		// System.out.println("SUCCESS");
		// System.out.println("-------------------------------------------------------------------------------------------");
		// System.out.println();
		// System.out.println();

	}

	private static void checkGameAdded(int boardID, String gameUri2) {

		String json = HttpService.get(gameUri2, HttpURLConnection.HTTP_OK);
		System.out.println(json);

	}

	private static Map<String, Service> getNeededServices(String type) {
		Map<String, Service> services = new HashMap<>();

		// services.put(ServiceNames.EVENT, start.getService(ServiceNames.EVENT));
		// ... weitere

		if (type.equals(ServiceNames.DICE) || type.equals(ServiceNames.BOARD)) {
			Service s = new Service("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
					"http://localhost:4567/events");

			services.put(ServiceNames.EVENT, s);
		}

		return services;
	}

}
