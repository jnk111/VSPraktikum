package vs.jan;

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

import vs.jonas.DiceService;

public class RunBoardExample {

	private static final Gson GSON = new Gson();
	private static final int TIMEOUT = 1000;
	private static BoardRESTApi boardApi;
	
	@SuppressWarnings("unused")
	private static DiceService diceApi;

	/**
	 * Startet den Boardservice und fuehrt ein paar Testoperationen aus.
	 * @param args
	 */
	public static void main(String[] args) {

		boardApi = new BoardRESTApi();
		diceApi = new DiceService(null);
		setupGame();

	}

	private static void setupGame() {
		setupBoard();

	}

	private static void setupBoard() {
		int boardID = 42;
		createBoard(boardID);
		placeBoard(boardID);
		checkBoardAdded(boardID);
		createPawns(boardID);
		movePawns(boardID);
		updatePawns(boardID);
		deletePawns(boardID);
		putPlaces(boardID);
		getFinalBoardState(boardID);
							

	}

	private static void getFinalBoardState(int boardID) {
		System.out.println("FINAL BOARD STATE: ");
		System.out.println("-------------------------------------------------------------------------------------------");
		checkBoardAdded(boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println("FINISHED");
		System.out.println();
		
		
	}

	private static void createPawns(int boardID) {
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

	private static void putPlaces(int boardID) {
		
		System.out.println("Update place information of a random place");
		System.out.println("-------------------------------------------------------------------------------------------");

		try {
			Thread.sleep(TIMEOUT);
			List<String> places = getPlacesUris(boardID);
			int index = (int) (Math.random() * places.size());
			String pUri = places.get(index);
			JSONPlace place = getPlace(pUri);
			Place p = new Place(pUri, place.getName(), place.getBroker());
			p.setName("Los");
			p.setBrokerUri("/broker/places/" + p.getName().toLowerCase());
			System.out.println("Place that will be updated: " + GSON.toJson(place));
			URL url = new URL("http://localhost:4567" + pUri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			System.out.println("Insert Placename and Brokeruri...");
			String json = GSON.toJson(p.convert());
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			System.out.println();
			System.out.println("Updated Place..." + GSON.toJson(getPlace(pUri)));

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

	private static JSONPlace getPlace(String placeUri) {

		try {

			System.out.println("Get Place with uri: " + placeUri + " ...");
			URL url = new URL("http://localhost:4567" + placeUri);
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
				JSONPlace place = GSON.fromJson(response.toString(), JSONPlace.class);
				System.out.println("Received Place: " + GSON.toJson(place));
				System.out.println();
				return place;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<String> getPlacesUris(int boardID) {
		try {
			System.out.println("Get Places...");
			URL url = new URL("http://localhost:4567/boards/" + boardID + "/places");
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
				
				@SuppressWarnings("unchecked")
				List<String> places = GSON.fromJson(response.toString(), List.class);
				System.out.println("Received Places: " + GSON.toJson(places));
				System.out.println();
				return places;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static void deletePawns(int boardID) {
		System.out.println("Delete a random pawn on the board...");
		System.out.println("-------------------------------------------------------------------------------------------");

		try {
			Thread.sleep(TIMEOUT);
			System.out.println("Old PawnList:");
			JSONPawnList list = getPawnsOnBoard(boardID);
			
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
		getPawnsOnBoard(boardID);
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

	private static void deletePawn(JSONPawn pawn, int boardID) {

		try {
			System.out.println("Pawn that will be deleted: " + GSON.toJson(pawn));
			URL url = new URL("http://localhost:4567" + pawn.getId());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			System.out.println();

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

		try {

			System.out.println("Pawn that will be updated: " + GSON.toJson(pawn));
			URL url = new URL("http://localhost:4567" + pawn.getId());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			System.out.println("Update PlayerUri...");
			pawn.setPlayer("/games/" + boardID + "/players/updated");
			String json = GSON.toJson(pawn);
			System.out.println("Sending JSON-Body: " + json);
			wr.writeBytes(json);
			wr.flush();
			wr.close();
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void movePawns(int boardID) {
		System.out.println("Move the Pawns to a new Position on the board");
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
			move(p);
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

	private static void move(JSONPawn p) {
		try {

			Thread.sleep(TIMEOUT);
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
			Thread.sleep(TIMEOUT);
			System.out.println("Get Pawn from Board with URI " + pawnUri + "...");
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
			Thread.sleep(TIMEOUT);
			System.out.println("Get Pawns from Board with id " + boardID + "...");
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
			Thread.sleep(TIMEOUT);
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

		try {
			Thread.sleep(TIMEOUT);
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
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();
	}

	private static void checkBoardAdded(int boardID) {
		try {
			Thread.sleep(TIMEOUT);
			System.out.println("Get Board with id " + boardID + "...");
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

			Thread.sleep(TIMEOUT);
			System.out.println("Create Board");
			System.out.println("-------------------------------------------------------------------------------------------");
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
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println();

	}

}
