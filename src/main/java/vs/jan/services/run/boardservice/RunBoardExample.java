package vs.jan.services.run.boardservice;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.broker.BrokerAPI;
import vs.jan.api.decksservice.DecksAPI;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.exception.ResponseCodeException;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPawnList;
import vs.jan.json.boardservice.JSONService;
import vs.jan.model.ServiceNames;
import vs.jan.model.boardservice.Player;
import vs.jan.tools.HttpService;
import vs.jonas.services.services.DiceService;
import vs.jonas.services.services.EventService;
import vs.malte.example.json.CreateGameExDTO;
import vs.malte.services.GamesServiceAPI;

public class RunBoardExample {

	private static final Gson GSON = new Gson();
	private static final int TIMEOUT = 500;
	private static final String HOST = "http://localhost:4567";
	private final static String GAME_URI = HOST + "/games";
	private final static String BOARD_URI = HOST + "/boards";
	private final static String BANK_URI = HOST + "/banks";
	private final static int BOARD_ID = 42;
	private final static int MAX_PLAYERS = 4;
	private static final String BROKER_URI = HOST + "/broker";

	/**
	 * Startet den Boardservice und fuehrt ein paar Testoperationen aus.
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws MalformedURLException
	 * @throws ResponseCodeException 
	 */
	public static void main(String[] args) throws InterruptedException, MalformedURLException, ResponseCodeException {
		initialize();
		setupGame();
	}

	private static void initialize() {
		
		BankService.run();
		new GamesServiceAPI();
		Map<String, JSONService> neededServicesDice = getNeededServices(ServiceNames.DICE);
		new EventService().startService();
		new DiceService(neededServicesDice).startService();
		new UserServiceRESTApi();
		new BrokerAPI();
		new BoardRESTApi();
		new DecksAPI();
	}

	private static void setupGame() throws InterruptedException, MalformedURLException, ResponseCodeException {
		setupBoard();

	}

	private static void setupBoard() throws InterruptedException, MalformedURLException, ResponseCodeException {

		createBoard();
		createBank();
		setupUser();
		//startGame();
		letCurrPlayerRollDice();
		buyPlaces();
		getFinalBoardState();
	}


	private static void buyPlaces() {
		
		System.out.println("Buy Places: ");
		System.out.println("-----------------------------------------------------------");
		List<JSONPawn> pawns = getPawnList();
		
		for(JSONPawn p: pawns){
			buyPlace(p);
		}		
	}

	private static void buyPlace(JSONPawn p) {
		
		System.out.println();
		System.out.println("Pawn: " + p.getId() + " tries to buy the place: " + p.getPlace());
		String id = getID(p.getPlace());

		HttpService.post(BROKER_URI + "/" + BOARD_ID + "/places/" + id + "/owner", p.getPlayer(), 200);

		
		
	}

	private static List<JSONPawn> getPawnList() {
		String url = BOARD_URI + "/" + BOARD_ID + "/pawns";
		String json = HttpService.get(url, 200);
		JSONPawnList pawns = GSON.fromJson(json, JSONPawnList.class);
		List<JSONPawn> pawnlist = new ArrayList<>();
		for(String pawn: pawns.getPawns()){
			String json2 = HttpService.get(HOST + pawn , 200);
			JSONPawn p = GSON.fromJson(json2, JSONPawn.class);
			pawnlist.add(p);
		}
		return pawnlist;
	}

	private static void createBank() {
		JSONGameURI uri = new JSONGameURI("/games/" + BOARD_ID);
		System.out.println("Create the Bank");
		System.out.println("-------------------------------------------------------------------------------------------");
		HttpService.post(BANK_URI, uri, 200);
		System.out.println("Bank created");
		System.out.println(HttpService.get(BANK_URI, 200));
		System.out.println(HttpService.get(BANK_URI + "/42", 200));
		System.out.println("-------------------------------------------------------------------------------------------");
		
		
	}

	private static void letCurrPlayerRollDice() throws ResponseCodeException {
		
//		for(int i = 0; i < MAX_PLAYERS; i++){
//			System.out.println("Let Player with mutex roll the dice");
//			System.out.println("-------------------------------------------------------------------------------------------");
//			//String json = HttpService.get(GAME_URI + "/" + BOARD_ID + "/player/current", 200);
//			//Player p = GSON.fromJson(json, Player.class);
//			//HttpService.put(GAME_URI + "/" + BOARD_ID + "/player/turn", p, 201);
//			
//
//			String pawnUri = p.getPawn();
//			String [] u = pawnUri.split("/");
//			String id = u[u.length - 1];
//			String uri = BOARD_URI + "/" + BOARD_ID +"/pawns/" + id;
//			p.setPawn(uri);
//			System.out.println("Get Pawn with uri: " + p.getPawn());
//			String json2 = HttpService.get(p.getPawn(), 200);
//			JSONPawn pawn = GSON.fromJson(json2, JSONPawn.class);
//			System.out.println("Pawn rolls dice: " + json2);
//			String list = HttpService.post(HOST + pawn.getRoll(), null, 200);
//			System.out.println();
//			System.out.println("RECEIVED EVENTLIST: ");
//			System.out.println(list);
//			System.out.println();
//			System.out.println();
//			System.out.println("Player: " + json + " releases the Mutex");
//			HttpService.put(GAME_URI + "/" + BOARD_ID + "/players/" + id + "/ready", null, 200);
//		}
	}

//	private static void startGame() throws ResponseCodeException {
//		System.out.println("Start game: " + GAME_URI + "/" + BOARD_ID);
//		System.out.println("-------------------------------------------------------------------------------------------");
//		HttpService.put(GAME_URI + "/" + BOARD_ID + "/status", null, 200);
//		System.out.println();
//	}


	private static void setupUser() throws InterruptedException, ResponseCodeException {

		Thread.sleep(TIMEOUT);
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();
		p1.setUser("mario");
		p1.setUri("uri");
		p2.setUser("wario");
		p2.setUri("uri");
		p3.setUser("yoshi");
		p3.setUri("uri");
		p4.setUser("donkeykong");
		p4.setUri("uri");
		String gamePlayerUri = GAME_URI + "/" + BOARD_ID + "/players";
		System.out.println("Create Some Users on Game: " + BOARD_ID);
		System.out.println("-------------------------------------------------------------------------------------------");
		createUser(BOARD_ID, gamePlayerUri, p1);
		createUser(BOARD_ID, gamePlayerUri, p2);
		createUser(BOARD_ID, gamePlayerUri, p3);
		createUser(BOARD_ID, gamePlayerUri, p4);
		
		System.out.println();
		System.out.println("Create Bank Accounts:");
		System.out.println("-------------------------------------------------------------------------------------------");
		
		
//		System.out.println("Init Start balance:");
//		System.out.println("-------------------------------------------------------------------------------------------");
//		initStartBalances(gamePlayerUri, p1);
//		initStartBalances(gamePlayerUri, p2);
//		initStartBalances(gamePlayerUri, p3);
//		initStartBalances(gamePlayerUri, p4);
		
		System.out.println("Set Users ready on Game: " + BOARD_ID);
		System.out.println("-------------------------------------------------------------------------------------------");
		setUserReady(p1, BOARD_ID);
		setUserReady(p2, BOARD_ID);
		setUserReady(p3, BOARD_ID);
		setUserReady(p4, BOARD_ID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		checkPlayersAddedOnUserService();
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();

	}


	private static String getID(String uri) {
		String [] u = uri.split("/");
		String id = u[u.length - 1];
		return id;
	}

	private static void setUserReady(Player p1, int boardID) throws InterruptedException, ResponseCodeException {
		
		Thread.sleep(TIMEOUT);
		String json = HttpService.get(GAME_URI + "/" + BOARD_ID + "/players/" + getID(p1.getUser()), 200);
		System.out.println("User is ready: " + json);
		Player p = GSON.fromJson(json, Player.class);
		String id = getID(p.getId());
		HttpService.put( GAME_URI + "/" + BOARD_ID + "/players/" + id + "/ready", null, 200);
		System.out.println("SUCCESS");
		System.out.println();
	}

	private static void checkPlayersAddedOnUserService() throws ResponseCodeException {

		System.out.println("Check players added to Userservice...");
		String userServUri = "http://localhost:4567/users";
		System.out.println("Added Users: " + HttpService.get(userServUri, HttpURLConnection.HTTP_OK));

	}

	private static void createUser(int boardID, String gamePlayerUri, Player player) throws InterruptedException, ResponseCodeException {
		Thread.sleep(TIMEOUT);
		HttpService.post(gamePlayerUri, player, HttpURLConnection.HTTP_CREATED);
		System.out.println("Created Player: " + GSON.toJson(player));
		System.out.println();
	}

	private static void getFinalBoardState() throws InterruptedException, ResponseCodeException {
		System.out.println("FINAL BOARD STATE: ");
		System.out.println("-------------------------------------------------------------------------------------------");
		checkBoardAdded(BOARD_ID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println("FINISHED");
		System.out.println();

	}







	private static void checkBoardAdded(int boardID) throws InterruptedException, ResponseCodeException {

		Thread.sleep(TIMEOUT);
		System.out.println("Get Board with id " + boardID + "...");
		String json = HttpService.get("http://localhost:4567/boards/" + boardID, HttpURLConnection.HTTP_OK);
		JSONBoard board = GSON.fromJson(json, JSONBoard.class);
		System.out.println("SUCCESS");
		System.out.println("Board: " + GSON.toJson(board));
		System.out.println();

	}

	private static void createBoard() throws InterruptedException, ResponseCodeException {

		Thread.sleep(TIMEOUT);
		System.out.println("Create Game on: " + BOARD_ID);
		CreateGameExDTO g = new CreateGameExDTO();
		g.setName("" + BOARD_ID);
		HttpService.post(GAME_URI, g, HttpURLConnection.HTTP_CREATED);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
	}


	private static Map<String, JSONService> getNeededServices(String type) {
		
		Map<String, JSONService> services = new HashMap<>();

		if (type.equals(ServiceNames.DICE) || type.equals(ServiceNames.BOARD)) {
			JSONService s = new JSONService("/services/13", "Logs the Events", "bla", ServiceNames.EVENT, "running",
					"http://localhost:4567/events");

			services.put(ServiceNames.EVENT, s);
		}

		return services;
	}

}