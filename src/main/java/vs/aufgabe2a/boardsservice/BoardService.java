package vs.aufgabe2a.boardsservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.aufgabe1.diceservice.Dice;
import vs.aufgabe1.userservice.User;
import vs.aufgabe1b.models.Event;
import vs.aufgabe2a.boardsservice.exceptions.ConnectionRefusedException;
import vs.aufgabe2a.boardsservice.exceptions.InvalidInputException;
import vs.aufgabe2a.boardsservice.exceptions.MutexPutException;
import vs.aufgabe2a.boardsservice.exceptions.ResourceNotFoundException;
import vs.aufgabe2a.boardsservice.exceptions.TurnMutexNotFreeException;
import vs.aufgabe2a.boardsservice.models.Board;
import vs.aufgabe2a.boardsservice.models.Field;
import vs.aufgabe2a.boardsservice.models.Pawn;
import vs.aufgabe2a.boardsservice.models.Place;
import vs.aufgabe2a.boardsservice.models.json.JSONBoard;
import vs.aufgabe2a.boardsservice.models.json.JSONBoardList;
import vs.aufgabe2a.boardsservice.models.json.JSONField;
import vs.aufgabe2a.boardsservice.models.json.JSONGameURI;
import vs.aufgabe2a.boardsservice.models.json.JSONPawn;
import vs.aufgabe2a.boardsservice.models.json.JSONPawnList;
import vs.aufgabe2a.boardsservice.models.json.JSONPlace;
import vs.aufgabe2a.boardsservice.models.json.JSONThrowsList;
import vs.aufgabe2a.boardsservice.models.json.JSONThrowsURI;

public class BoardService {

	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;

	/*
	 * Uri-Liste der gemachten Wuerfe
	 * JSONThrowsUri -> die URI der von einer Pawn gemachten Wuerfe 
	 * JSONThrowsList -> die Werte der Wuerfel
	 */
	private Map<JSONThrowsURI, JSONThrowsList> throwMap;

	/**
	 * Defaultkonstruktor
	 */
	public BoardService() {
		boards = new HashMap<>();
	}

	/**
	 * Liefert alle Board-Uris, die dem Spiel zugeteilt wurden
	 * 
	 * @return Liste der Board-Uris als JSON-DTO
	 */
	public JSONBoardList getAllBoardURIs() {

		JSONBoardList boardURIs = new JSONBoardList();
		boards.forEach((k, v) -> boardURIs.addBoardURI(k.getUri()));
		return boardURIs;
	}

	/**
	 * Erzeugt ein neues Board fuer die uebergebene Gameid, falls dieses Spiel
	 * vorhanden ist.
	 * 
	 * @param game
	 *          Das Spiel, fuer das ein Board erzeugt werden soll
	 * @throws InvalidInputException
	 *           Json-Format der Uri ungueltig
	 */
	public synchronized void createNewBoard(JSONGameURI game) throws InvalidInputException {

		if (game.isValid()) {
			String boardUri = "/boards/" + game.getURI().split("/")[2];
			boards.put(new Board(boardUri), game);
		} else {
			throw new InvalidInputException();
		}
	}

	/**
	 * Liefert das einer Gameid zugehoerige Board, falls vorhanden
	 * 
	 * @param gameid
	 *          Die Gameid
	 * @return Das zugehoerige Board als JSON-DTO
	 * @throws ResourceNotFoundException
	 *           Board wurde nicht gefunden
	 */
	public JSONBoard getBoardForGame(String gameid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);

		if (b != null) {
			return (JSONBoard) getBoard(gameid).convert();
		}
		throw new ResourceNotFoundException();

	}

	/**
	 * Erzeugt eine neue Spiefigur auf dem Board
	 * 
	 * @param pawn
	 *          D Die Figur als JSON-DTO
	 * @param gameid
	 *          Das Spiel fuer das die Figur erzeugt werden soll
	 * @throws InvalidInputException
	 *           Ungueltige JSON-DTO (z. B. required-Parameter fehlen)
	 * @throws ResourceNotFoundException
	 *           Es konnte kein Board zum Spiel gefunden werden
	 */
	public synchronized void createNewPawnOnBoard(JSONPawn pawn, String gameid)
			throws InvalidInputException, ResourceNotFoundException {

		Board b = getBoard(gameid);
		System.out.println(b);

		if (b != null) {
			if (pawn.isValid()) {
				Pawn p = new Pawn();
				String pawnUri = getPawnUri(b, pawn.getPlayer());
				p.setPawnUri(pawnUri);
				p.setMovesUri(p.getPawnUri() + "/move");
				p.setPlaceUri(pawn.getPlace()); // Annahme required
				p.setPlayerUri(pawn.getPlayer()); // Annahme required
				p.setPosition(pawn.getPosition()); // Annahme required
				p.setRollsUri(p.getPawnUri() + "/roll");
				b.addPawn(p);
				
				// Neue Wuerfelliste fuer die Figur erstellen
				JSONThrowsURI uri = new JSONThrowsURI(p.getRollsUri());
				JSONThrowsList list = new JSONThrowsList();
				throwMap.put(uri, list);
				
			} else {
				throw new InvalidInputException();
			}
		} else {
			throw new ResourceNotFoundException();
		}

	}


	/**
	 * Liefert alle Pawn-Uris, die auf dem Spielbrett sind
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @return Liste von Pawn-Uris als JSON-Dto
	 * @throws ResourceNotFoundException
	 *           Board wurde nicht gefunden
	 */
	public JSONPawnList getPawnsOnBoard(String gameid) throws ResourceNotFoundException {

		JSONPawnList pl = new JSONPawnList();
		Board b = getBoard(gameid);

		if (b != null) {
			for (Field f : b.getFields()) {
				for (Pawn p : f.getPawns()) {
					pl.addPawnURI(p.getPawnUri());
				}
			}
			return pl;
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Liefert eine bestimmte Spielfigur als JSON-DTO
	 * 
	 * @param gameid
	 *          Die Gameid des Boards auf dem die Figur steht
	 * @param pawnid
	 *          Die Pawn-ID um die Figur eindeutig zu identifizieren
	 * @return Die Pwan, als JSON-DTO
	 * @throws ResourceNotFoundException
	 *           Board oder Spielfigur konnte nicht gefunden werden
	 */
	public JSONPawn getSpecificPawn(String gameid, String pawnid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);
		if (b != null) {
			for (Field f : b.getFields()) {
				for (Pawn p : f.getPawns()) {
					if (p.getPawnUri().contains(pawnid)) {
						return (JSONPawn) p.convert();
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Liefert alle Wuerfe die von einem Spieler fuer seine Figur bereits gemacht
	 * wurden, als Json-DTO
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @param pawnid
	 *          Die Pawnid der Figur
	 * @return JSONThrowsList Die Liste der Wuerfe zu dieser Figur als Json-DTO
	 * @throws ResourceNotFoundException
	 *           Board oder Pawn nicht gefunden
	 */
	public JSONThrowsList getDiceThrows(String gameid, String pawnid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);

		if (b != null) {
			for (JSONThrowsURI uri : throwMap.keySet()) {
				if (uri.getRollUri().contains(pawnid)) {
					return throwMap.get(uri);
				}
			}
		}

		throw new ResourceNotFoundException();
	}

	/**
	 * Liefert das Board zu einer Gameid, muss spaeter erweitert werden, falls
	 * mehere Boards einem Spiel zugeteilt werden koennen.
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @return Board Das Board zu der Gameid
	 */
	private Board getBoard(String gameid) {

		for (Board b : boards.keySet()) {
			if (boards.get(b).getURI().contains(gameid)) {
				return b;
			}
		}
		return null;
	}

	/**
	 * Ermittelt alle Felder auf dem Board, erzeugt jeweils URI und gibt eine
	 * Liste mit ermittelten URis zurueck.
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @return List<String> Die Uri-Liste der Felder auf dem Board
	 * @throws ResourceNotFoundException
	 *           Das Board wurde nicht gefunden
	 */
	public List<String> getAllPlaces(String gameid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);
		if (b != null) {
			List<String> allPlaceURIs = new ArrayList<>();

			for (Field f : b.getFields()) {
				allPlaceURIs.add(f.getPlace().getPlaceUri());
			}
			return allPlaceURIs;
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Gibt Informationen ueber einen bestimmten Place zurueck (Das ist nicht das
	 * gesamte Feld, sondern nur die dem Feld zugeteilte Straße)
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @param placeid
	 *          Die Placeid
	 * @return JSONPlace Json-DTO des Places
	 * 
	 * @throws ResourceNotFoundException
	 *           Board oder Place nicht gefunden
	 * 
	 */
	public JSONPlace getSpecificPlace(String gameid, String placeid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);
		if (b != null) {
			for (Field f : b.getFields()) {
				if (f.getPlace().getPlaceUri().contains(placeid)) {
					return (JSONPlace) f.getPlace().convert();
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Bewegt eine Spielfigur um den Wert der <code>rollValue</code> vorwaerts
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @param pawnid
	 *          Die Pawn-ID der Figur
	 * @param rollValue
	 *          Der Wert um den die Figur bewegt werden soll
	 * @throws ResourceNotFoundException
	 *           Board oder Figur nicht gefunden
	 * @throws InvalidInputException
	 *           Es wurde keine gueltiger Wurf uebergeben
	 */
	public synchronized void movePawn(String gameid, String pawnid, int rollValue) 
			throws ResourceNotFoundException, InvalidInputException {

		Board b = getBoard(gameid);

		if (b != null) {
			if(rollValue > 0){
				for (Field f : b.getFields()) {
					for (Pawn p : f.getPawns()) {
						if (p.getPawnUri().contains(pawnid)) {
							p.setPosition(p.getPosition() + rollValue);
						// TODO Event posten
							return;
						}
					}
				}
			}else{
				throw new InvalidInputException();
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Uebergibt einen Wurf an das Board und fuehrt weitere noetige Aktonen aus.
	 * 
	 * @param gameid
	 *          Das Board der Gameid
	 * @param pawnid
	 *          Die Pawn-Id der Figur, fuer die gewuerfelt wird
	 *          
	 * @return List<Event>
	 * 					Liste aller Events die mit diesem Wurf stattgefunden haben
	 * @throws ResourceNotFoundException
	 *           Board oder Figur nicht gefunden
	 *           
	 */
	public synchronized List<Event> rollDice(String gameid, String pawnid) throws ResourceNotFoundException {

		Board board = getBoard(gameid);
		Pawn pawn = getPawn(board, pawnid);

		if (pawn != null && board != null) {
			checkPlayerHasTurn(pawn, gameid);
			putPlayersTurn(pawn, gameid);
			int rollValue = doDiceRoll(pawn, gameid);
			movePawnRESTCall(gameid, pawnid, rollValue);
			placeAPawnRESTCall(gameid, pawn);
			
			// weitere Aktionen...
			
			return retrieveEventList(pawn, gameid, new Date());
			
		} else {
			throw new ResourceNotFoundException();
		}
	}


	private int doDiceRoll(Pawn pawn, String gameid) {
		
		try {
			Dice roll = null;
			User user = getPlayer(pawn, gameid);
			URL url = new URL("http://localhost:4567/dice?" + "player=" + user.getId() + "&uri=" + user.getUri());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				roll = new Gson().fromJson(response.toString(), Dice.class);
				addThrowToPawnThrowList(pawn, roll);
				
				// TODO: Event posten
				
				return roll.getNumber();
			}
		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}
		
		return -1;

	}

	/**
	 * Ermittelt den Spieler zu der Figur vom Game-Service
	 * 
	 * @param pawn
	 *          Die Figur des Spielers
	 * @param gameid
	 *          Die Gameid zum Spiel
	 * @return User Der Spieler der wuerfeln moechte
	 * @throws ResourceNotFoundException
	 * 					Spieler wurde nicht gefunden
	 * @throws ConnectionRefusedException
	 * 					Service nicht erreichbar
	 */
	private User getPlayer(Pawn pawn, String gameid)
			throws InvalidInputException, ResourceNotFoundException, ConnectionRefusedException {
		try {
			// z. B.: 'http://localhost:4567/games/42/players/games/42/players/mario
			User currPlayer = null;
			URL url = new URL("http://localhost:4567/games/" + gameid + "/players" + pawn.getPlayerUri());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			StringBuffer response = new StringBuffer();

			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
			currPlayer = new Gson().fromJson(response.toString(), User.class);

			if (currPlayer != null && currPlayer.isValid()) {
				return currPlayer;
			} else {
				throw new ResourceNotFoundException();
			}

		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}
	}

	/**
	 * Teilt den Turn-Mutex dem gerade wuerfelnden Spieler zu, alle anderen
	 * Spieler koennen nicht mit dem Board agieren, solange ein anderer Spieler
	 * den Mutex haelt.
	 * 
	 * @param pawn
	 *          Die Figur des Spielers, der den Mutex erhaelt
	 * @param gameid
	 *          Die Gameid des Boards
	 * @throws MutexPutException
	 *           Ein anderer Spieler haelt den Mutex, bzw. der Mutex ist nicht
	 *           frei
	 * @throws InvalidInputException
	 *           Board oder Pawn wurde nicht gefunden -> Abbruch
	 * @throws ConnectionRefusedException
	 *           Service ist nicht erreichbar -> Abbruch
	 */
	private void putPlayersTurn(Pawn pawn, String gameid)
			throws MutexPutException, InvalidInputException, ConnectionRefusedException {

		try {
			URL url = new URL("http://localhost:4567/games/" + gameid + "/turn");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.getOutputStream().write(pawn.getPlayerUri().getBytes());
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new MutexPutException();
			}
			
			// TODO: Event posten
		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}

	}

	private Pawn getPawn(Board board, String pawnid) {

		for (Field f : board.getFields()) {
			for (Pawn p : f.getPawns()) {
				if (p.getPawnUri().contains(pawnid)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Prueft ob bereits ein anderer Spieler den Mutex haelt.
	 * 
	 * @param pawn
	 *          Die Spielfigur fuer die gewuerfelt werden soll
	 * @param gameid
	 *          Die Gameid des Boards
	 * 
	 * @throws TurnMutexNotFreeException
	 *           Ein anderer Spieler ist an der Reihe -> Abbruch
	 * @throws ConnectionRefusedException
	 *           Service ist nicht erreichbar -> Abbruch
	 * @throws InvalidInputException
	 *           Board oder Pawn wurde nicht gefunden -> Abbruch
	 */
	private void checkPlayerHasTurn(Pawn pawn, String gameid)
			throws TurnMutexNotFreeException, ConnectionRefusedException, InvalidInputException {

		try {
			User currPlayer = null;
			URL url = new URL("http://localhost:4567/games/" + gameid + "/turn");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuffer response = new StringBuffer();
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();
				currPlayer = new Gson().fromJson(response.toString(), User.class);

				if (currPlayer != null && currPlayer.isValid()) {
					throw new TurnMutexNotFreeException();
				}
			}
		} catch (MalformedURLException mfe) {
			throw new InvalidInputException();
		} catch (IOException ioe) {
			throw new ConnectionRefusedException();
		}
	}

	/**
	 * Loescht eine Spielfigur vom Board (z. B. bei verlassen des Spiels vor Ende)
	 * 
	 * @param gameid
	 *          Die dem Board zugeteilte Gameid
	 * @param pawnid
	 *          Die Pawn-ID der Figur, die geloescht werden soll
	 * @throws ResourceNotFoundException
	 *           Baord oder Figur nicht gefunden
	 */
	public synchronized void deletePawnFromBoard(String gameid, String pawnid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);
		if (b != null) {
			for (Field f : b.getFields()) {
				for (Pawn p : f.getPawns()) {
					if (p.getPawnUri().contains(pawnid)) {
						f.getPawns().remove(p);
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Loescht das Board bei Beendigung des Spiels
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @throws ResourceNotFoundException
	 *           Das Board wurde nicht gefunden
	 */
	public synchronized void deleteBoard(String gameid) throws ResourceNotFoundException {

		Board b = getBoard(gameid);

		if (b != null) {
			boards.remove(b);
			return;
		}

		throw new ResourceNotFoundException();

	}

	/**
	 * Verandert Feldinformationen auf dem Board (z. B. eine Straße erhaelt einen
	 * neue Broker-Uri)
	 * 
	 * @param place
	 *          Der Place als Json-DTO
	 * @param pathinfo
	 *          die Uri des Feldes
	 * @param gameid
	 *          Die Gameid des Boardes
	 * @throws ResourceNotFoundException
	 *           Board oder Place nicht gefunden
	 */
	public synchronized void updateAPlaceOnTheBoard(JSONPlace place, String pathinfo, String gameid)
			throws ResourceNotFoundException {

		Board key = getBoard(gameid);
		if (place.isValid() && key != null) {
			for (Field f : key.getFields()) {
				Place p = f.getPlace();
				if (p.getPlaceUri().equals(pathinfo)) {
					p.setName(place.getName());
					p.setBrokerUri(place.getBroker());
					return;
				}
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Befuellt ein Board mit Feldern
	 * 
	 * @param gameid
	 *          Die Gameid des Boards
	 * @param board
	 *          Das Board als Json-DTO
	 * @throws ResourceNotFoundException
	 *           Das Board wurde nicht gefunden
	 */
	public synchronized void placeABoard(String gameid, JSONBoard board) throws ResourceNotFoundException {

		Board key = getBoard(gameid);

		if (board.isValid() && key != null) {
			List<Pawn> pawns = new ArrayList<>();
			List<Field> fields = new ArrayList<>();
			Pawn pawn = null;

			for (JSONField f : board.getFields()) {
				Field field = new Field();
				pawns = new ArrayList<>();
				for (String pawnUri : f.getPawns()) {
					pawn = new Pawn();
					pawn.setPawnUri(pawnUri);
					pawn.setPlaceUri(f.getPlace());
					pawn.setMovesUri(pawnUri + "/move");
					pawn.setPosition(0); // Startposition
					pawn.setRollsUri(pawnUri + "/roll");
					pawns.add(pawn);
				}
				Place place = new Place();
				place.setPlaceUri(f.getPlace());
				field.setPawns(pawns);
				field.setPlace(place);
				fields.add(field);
			}

			key.setFields(fields);
			key.setPlayers("/boards/" + gameid + "/players");
			key.setPositions(board.getPositions()); // TODO: nachfragen

			JSONGameURI entry = boards.get(key);
			boards.put(key, entry);
		} else {
			throw new InvalidInputException();
		}
	}

	/**
	 * Weist einer Figur ein neues Feld zu (z. B. nach Wuerfeln)
	 * 
	 * @param gameid
	 *          Die Gameid des Boardes
	 * @param pawn
	 *          Die Figur als Json-DTO
	 * @throws ResourceNotFoundException
	 *           Das Board oder die Figur wurde nicht gefunden
	 */
	public synchronized void placeAPawn(String gameid, JSONPawn pawn) throws ResourceNotFoundException {

		Board b = getBoard(gameid);

		if (pawn.isValid() && b != null) {
			for (Field f : b.getFields()) {
				for (Pawn p2 : f.getPawns()) {
					if (p2.getPawnUri().contains(pawn.getId())) {
						p2.setMovesUri(pawn.getMove());
						p2.setPawnUri(pawn.getId());
						p2.setPlaceUri(pawn.getPlace());
						p2.setPlayerUri(pawn.getPlayer());
						p2.setPosition(pawn.getPosition());
						p2.setRollsUri(pawn.getRoll());
						
						// TODO Event posten
						
						return;
					}
				}
			}
		}
		throw new ResourceNotFoundException();
	}
	
	
	/**
	 * Fuegt einen neuen Wurf zu der Wurfliste die der aktuell wuerfelnden Figur
	 * zugeteilt ist, hinzu
	 * @param pawn
	 * 				Die aktuelle Figur, fuer die gewuerfelt wird
	 * @param roll
	 * 				Der Wuerfel
	 */
	private void addThrowToPawnThrowList(Pawn pawn, Dice roll) {
		JSONThrowsURI throwUri = new JSONThrowsURI(pawn.getRollsUri());
		JSONThrowsList list = throwMap.get(throwUri);
		list.addThrow(roll);
	}
	
	/**
	 * TODO: implementiere URI-Erzeugung Generiert die Pawn-Uri aus dem
	 * GET-Response der PlayerUri
	 * 
	 * @param board
	 *          The Board
	 * @param playerUri
	 *          The PlayerUri
	 * @return Pawn-Uri
	 */
	private String getPawnUri(Board board, String playerUri) {

		// GET playerUri
		String boardUri = board.getUri();
		return boardUri + "/pawns/" + ((int) (Math.random() * 20));

	}
	
	
	/**
	 * TODO: implement
	 * REST-Aufruf um einer Figur ein neues Feld zuzuweisen, nachdem sie bewegt wurde
	 * wird von rollDice() aufgerufen
	 * @param gameid
	 * @param pawn
	 */
	private void placeAPawnRESTCall(String gameid, Pawn pawn) {
		
	}

	/**
	 * TODO: implement
	 * REST-Aufruf um einer Figur eine neue Position zuzuweisen, nachdem sie bewegt wurde
	 * wird von rollDice() aufgerufen
	 * @param gameid
	 * @param pawn
	 */
	private void movePawnRESTCall(String gameid, String pawnid, int rollValue) {
		
		
	}
	
	/**
	 * 
	 * Holt alle Events des Spielers seit dem letzten Wurf
	 * @param pawn
	 * 				Die Figur
	 * @param gameid
	 * 				Das Board
	 * @param date 
	 * 				Der aktuelle Zeitpunkt des Wurfes
	 * @return List<Event>
	 * 					Die Liste aller Events fuer diesen Wurf
	 * 	
	 */
	private List<Event> retrieveEventList(Pawn pawn, String gameid, Date date) {
		return new ArrayList<>();
	}
}
