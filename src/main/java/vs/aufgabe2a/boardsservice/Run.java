package vs.aufgabe2a.boardsservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import vs.aufgabe1.diceservice.DiceService;
import vs.aufgabe2a.boardsservice.exceptions.ConnectionRefusedException;
import vs.aufgabe2a.boardsservice.exceptions.InvalidInputException;
import vs.aufgabe2a.boardsservice.models.Board;
import vs.aufgabe2a.boardsservice.models.Field;
import vs.aufgabe2a.boardsservice.models.Pawn;
import vs.aufgabe2a.boardsservice.models.Place;
import vs.aufgabe2a.boardsservice.models.json.JSONBoard;
import vs.aufgabe2a.boardsservice.models.json.JSONGameURI;
import vs.aufgabe2a.boardsservice.models.json.JSONPawn;
import vs.aufgabe2a.boardsservice.models.json.JSONPawnList;

public class Run {

	
	private static final Gson GSON = new Gson();
	private static BoardRESTApi boardApi;
	@SuppressWarnings("unused")
	private static DiceService diceApi;
	public static void main(String[] args) {
	
		boardApi = new BoardRESTApi();
		diceApi = new DiceService();
		setupGame();

	}

	private static void setupGame() {
		setupBoard();
		
	}

	private static void setupBoard() {
		int boardID = 42;
		createBoard(boardID);
		placeBoard(boardID);
		
		// erzeuge zwei figuren auf 'Los'
		createPawn(boardID, "mario");
		createPawn(boardID, "wario");
		createPawn(boardID, "yoshi");
		createPawn(boardID, "donkeykong");
		movePawns(boardID);
		checkBoardAdded(boardID);
		
	}

	private static void movePawns(int boardID) {
		JSONPawnList list = getPawnsOnBoard(boardID);
		List<JSONPawn> pawns = new ArrayList<>();
		
		for(String pawnUri: list.getPawns()){
			JSONPawn p = getPawn(boardID, pawnUri);
			pawns.add(p);
		}
		
		System.out.println("Received Pawns: " + GSON.toJson(pawns));
		System.out.println();
		
		for(JSONPawn p: pawns){
			move(p);
		}
		pawns = new ArrayList<>();
		for(String pawnUri: list.getPawns()){
			JSONPawn p = getPawn(boardID, pawnUri);
			pawns.add(p);
		}
		System.out.println("Updated Pawns: " + GSON.toJson(pawns));
		
	}

	private static void move(JSONPawn p) {
		try {
			
			Thread.sleep(500);
			int rollValue = (int) ((Math.random() * 6) + 1);
			System.out.println("move Pawn with uri: " + p.getId());
			URL url = new URL("http://localhost:4567" + p.getMove());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String json = GSON.toJson(rollValue);
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			
			int respCode = connection.getResponseCode();
			System.out.println("Response Code: " + respCode);

		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		
		
	}

	private static JSONPawn getPawn(int boardID, String pawnUri) {
		try {
			Thread.sleep(500);
			System.out.println("Get Pawn from Board with URI "+ pawnUri + "...");
			URL url = new URL("http://localhost:4567" + pawnUri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				JSONPawn pawn = GSON.fromJson(response.toString(), JSONPawn.class);
				System.out.println("Received Pawn: " + GSON.toJson(pawn));
				System.out.println();
				return pawn;
			}

		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static JSONPawnList getPawnsOnBoard(int boardID) {
		try {
			Thread.sleep(500);
			System.out.println("Get Pawns from Board with id "+ boardID + "...");
			URL url = new URL("http://localhost:4567/boards/" + boardID + "/pawns");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				JSONPawnList list = GSON.fromJson(response.toString(), JSONPawnList.class);
				System.out.println("Received PawnUris: " + GSON.toJson(list));
				System.out.println();
				return list;
			}

		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void createPawn(int boardID, String playerName) {
		try {
			Pawn p = new Pawn();
			p.setPlayerUri("/games/" + boardID + "/players/" + playerName);
			p.setPlaceUri("/boards/" + boardID + "/places/0");
			Thread.sleep(500);
			System.out.println("Create Pawn...");
			URL url = new URL("http://localhost:4567/boards/" + boardID + "/pawns");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String json = GSON.toJson(p.convert());
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			
			int respCode = connection.getResponseCode();
			System.out.println("Response Code: " + respCode);

		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		
	}

	private static void placeBoard(int boardID) {
		
		checkBoardAdded(boardID);
		Board b = boardApi.getBoardService().getBoard("" + boardID);
		
		for(int i = 0; i <= 10; i++){
			Field f = new Field();
			Place p = new Place("/boards/42/places/" + i);
			f.setPlace(p);
			b.getFields().add(f);
		}
		
		try {
			Thread.sleep(500);
			System.out.println("Put Board...");
			URL url = new URL("http://localhost:4567/boards/" + boardID);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String json = GSON.toJson(b.convert());
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			
			int respCode = connection.getResponseCode();
			System.out.println("Response Code: " + respCode);

		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}

	
	private static void checkBoardAdded(int boardID) {
		try {
			Thread.sleep(500);
			System.out.println("Get Board with id "+ boardID + "...");
			URL url = new URL("http://localhost:4567/boards/" + boardID);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				JSONBoard board = GSON.fromJson(response.toString(), JSONBoard.class);
				System.out.println("Received Board: " + GSON.toJson(board));
			}

		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		
	}

	private static void createBoard(int boardID) {
		try {
			
			Thread.sleep(500);
			System.out.println("Create Board...");
			URL url = new URL("http://localhost:4567/boards");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			JSONGameURI gameid = new JSONGameURI("/games/" + boardID);
			String json = GSON.toJson(gameid);
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			
			int respCode = connection.getResponseCode();
			System.out.println("Response Code: " + respCode);

		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		
	}

}
