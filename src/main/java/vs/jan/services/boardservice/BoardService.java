package vs.jan.services.boardservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

import vs.jan.exceptions.ConnectionRefusedException;
import vs.jan.exceptions.InvalidInputException;
import vs.jan.exceptions.MutexPutException;
import vs.jan.exceptions.ResourceNotFoundException;
import vs.jan.exceptions.TurnMutexNotFreeException;
import vs.jan.models.Board;
import vs.jan.models.Field;
import vs.jan.models.Pawn;
import vs.jan.models.Place;
import vs.jan.models.Service;
import vs.jan.models.ServiceNames;
import vs.jan.models.User;
import vs.jan.models.json.JSONBoard;
import vs.jan.models.json.JSONBoardList;
import vs.jan.models.json.JSONField;
import vs.jan.models.json.JSONGameURI;
import vs.jan.models.json.JSONPawn;
import vs.jan.models.json.JSONPawnList;
import vs.jan.models.json.JSONPlace;
import vs.jan.models.json.JSONThrowsList;
import vs.jan.models.json.JSONThrowsURI;
import vs.jan.tools.HttpService;
import vs.jonas.services.json.EventData;
import vs.jonas.services.model.Dice;
import vs.jonas.services.model.Event;

public class BoardService {

	private final Gson GSON = new Gson();
	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;

	/*
	 * Uri-Liste der gemachten Wuerfe JSONThrowsUri -> die URI der von einer Pawn
	 * gemachten Wuerfe JSONThrowsList -> die Werte der Wuerfel
	 */
	private Map<JSONThrowsURI, JSONThrowsList> throwMap;

	private Map<String, Service> neededServices;

	/**
	 * Defaultkonstruktor
	 */
	public BoardService() {
		this.neededServices = getNeededServices(ServiceNames.BOARD);
		boards = new HashMap<>();
		throwMap = new HashMap<>();
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
				b.addNewPawn(p);

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
	public Board getBoard(String gameid) {

		if (gameid != null) {
			for (Board b : boards.keySet()) {
				if (boards.get(b).getURI().contains(gameid)) {
					return b;
				}
			}
			return null;
		}
		throw new InvalidInputException();
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
			if (rollValue > 0) {
				for (Field f : b.getFields()) {
					List<Pawn> pawns = f.getPawns();
					for (int i = 0; i < pawns.size(); i++) {
						Pawn p = pawns.get(i);
						if (p.getPawnUri().contains(pawnid)) {
							int oldPos = p.getPosition(); // alte Position der Figur
							int newPos = oldPos + rollValue; // Neue Position der Figur

							// Eine Runde rumgelaufen?

							if (newPos >= b.getFields().size() - 1) {
								newPos = ((b.getFields().size() - 1) % newPos);
							}
							b.getFields().get(oldPos).removePawn(p); // Figur von alter
																												// Position entfernen
							p.setPosition(newPos); // setze neue Pos-Nr.
							b.getFields().get(newPos).addPawn(p); // Setze Figur auf neue
																										// Position
							b.updatePositions(oldPos, newPos); // markiere, dass auf Position
																									// 'newPos' Figuren stehen
							p.updatePlaceUri(newPos); // Update Placeuri to the new Place
							postEvent(gameid, "move", "move", p);
							return;

						}
					}
				}
			} else {
				throw new InvalidInputException();
			}
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Postet ein Event, das fuer eine Figur ausgeloest wurde z. B.: Figur nach
	 * einem Wuerfelwurd zu einer neuen Position bewegen
	 * 
	 * @param gameid
	 *          Die ID des Spiels
	 * @param type
	 *          Der Eventtyp, z. B.: 'move'
	 * @param name
	 *          Der Eventname
	 * @param pawn
	 *          Die Figur um die es sich handelt
	 */
	private void postEvent(String gameid, String type, String name, Pawn pawn) {

		Service service = this.neededServices.get(ServiceNames.EVENT);
		String reas = null;
		String resource = null;

		switch (type) {
		case "move": {
			reas = pawn.getPlayerUri() + " has moved the pawn: " + pawn.getPawnUri() + " to: " + pawn.getPlaceUri();
			resource = pawn.getRollsUri();
			break;
		}
		}

		EventData event = new EventData(gameid, type, name, reas, resource, pawn.getPlayerUri());
		HttpService.post(service.getUri(), event, HttpURLConnection.HTTP_OK);
	}

	/**
	 * Uebergibt einen Wurf an das Board und fuehrt weitere noetige Aktonen aus.
	 * 
	 * @param gameid
	 *          Das Board der Gameid
	 * @param pawnid
	 *          Die Pawn-Id der Figur, fuer die gewuerfelt wird
	 * 
	 * @return List<Event> Liste aller Events die mit diesem Wurf stattgefunden
	 *         haben
	 * @throws ResourceNotFoundException
	 *           Board oder Figur nicht gefunden
	 * 
	 */
	public synchronized List<Event> rollDice(String gameid, String pawnid) throws ResourceNotFoundException {

		Board board = getBoard(gameid);
		Pawn pawn = getPawn(board, pawnid);

		if (pawn != null && board != null) {
			// checkPlayerHasTurn(pawn, gameid);
			// putPlayersTurn(pawn, gameid);
			// int rollValue = doDiceRoll(pawn, gameid);

			int rollValue = doDiceRollLocal(pawn, gameid); // Zum Testen Local
			movePawn(gameid, pawnid, rollValue);

			// placeAPawnRESTCall(gameid, pawn);

			// weitere Aktionen...

			return retrieveEventList(pawn, gameid, new Date());

		} else {
			throw new ResourceNotFoundException();
		}
	}

	/**
	 * Fuehrt eine Wurfelaktion aus und fuegt den gemachten Wurd in die Wurfliste,
	 * die zu der Figur gehoert hinzu
	 * 
	 * @param pawn
	 *          Die Figur, fuer die gewuerfelt wird
	 * @param gameid
	 *          Die ID des Games
	 * @return Der Int-Wert des gemachten Wurfes
	 */
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
	 * Fuehrt eine Wurfelaktion aus und fuegt den gemachten Wurd in die Wurfliste,
	 * die zu der Figur gehoert hinzu
	 * 
	 * @param pawn
	 *          Die Figur, fuer die gewuerfelt wird
	 * @param gameid
	 *          Die ID des Games
	 * @return Der Int-Wert des gemachten Wurfes
	 */
	private int doDiceRollLocal(Pawn pawn, String gameid) {

		// http://localhost:4567/dice
		String playerUri = "http://localhost:4567/users/mario";
		String player = "mario";
		pawn.setPlayerUri(playerUri);

		User user = getPlayer(pawn, gameid);
		String json = HttpService.get("http://localhost:4567/dice?" + "player=" + playerUri + "&game=" + gameid,
				HttpURLConnection.HTTP_OK);
		Dice roll = GSON.fromJson(json, Dice.class);

		addThrowToPawnThrowList(pawn, roll);

		if (roll != null)
			return roll.getNumber();
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
	 *           Spieler wurde nicht gefunden
	 * @throws ConnectionRefusedException
	 *           Service nicht erreichbar
	 */
	private User getPlayer(Pawn pawn, String gameid) {
		// z. B.: 'http://localhost:4567/games/42/players/mario
		String json = HttpService.get(pawn.getPlayerUri(), HttpURLConnection.HTTP_OK);
		User currPlayer = GSON.fromJson(json, User.class);
		return currPlayer;
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
		HttpService.put("http://localhost:4567/games/" + gameid + "/turn", pawn, HttpURLConnection.HTTP_OK);
	}

	/**
	 * Gibt die Figur, die zu der Pawnid gehoert zurueck
	 * 
	 * @param board
	 *          Das Board auf dem die Figur steht
	 * @param pawnid
	 *          Die ID der Figur
	 * @return Die angeforderte Figur
	 */
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
	private synchronized void checkPlayerHasTurn(Pawn pawn, String gameid)
			throws TurnMutexNotFreeException, ConnectionRefusedException, InvalidInputException {

		String json = HttpService.get("http://localhost:4567/games/" + gameid + "/turn", HttpURLConnection.HTTP_OK);
		User currPlayer = GSON.fromJson(json, User.class);
		
		if (currPlayer != null && currPlayer.isValid()) {
			throw new TurnMutexNotFreeException();
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
						b.updatePositions(p.getPosition(), -1);
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

		if (key != null) {
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
	 * Weist einer Figur ein neues Feld zu (z. B. nach Wuerfeln) oder zu
	 * Debuggingzwecken
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
	 * 
	 * @param pawn
	 *          Die aktuelle Figur, fuer die gewuerfelt wird
	 * @param roll
	 *          Der Wuerfel
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

		String[] uri = playerUri.split("/");

		String id = uri[uri.length - 1];
		int num = 0;
		for (Field f : board.getFields()) {
			while (hasSamePawnID(f, id)) {
				num++;
				id = uri[uri.length - 1] + "_" + num;
			}
		}
		return board.getUri() + "/pawns/" + id;
	}

	private boolean hasSamePawnID(Field f, String pawnUri) {

		for (Pawn p : f.getPawns()) {
			if (p.getPawnUri().contains(pawnUri)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO: implement REST-Aufruf um einer Figur ein neues Feld zuzuweisen,
	 * nachdem sie bewegt wurde wird von rollDice() aufgerufen
	 * 
	 * @param gameid
	 * @param pawn
	 */
	private void placeAPawnRESTCall(String gameid, Pawn pawn) {

	}

	/**
	 * TODO: implement REST-Aufruf um einer Figur eine neue Position zuzuweisen,
	 * nachdem sie bewegt wurde wird von rollDice() aufgerufen
	 * 
	 * @param gameid
	 * @param pawn
	 */
	private void movePawnRESTCall(String gameid, String pawnid, int rollValue) {

	}

	/**
	 * Holt alle Events des Spielers seit dem letzten Wurf
	 * 
	 * @param pawn
	 *          Die Figur
	 * @param gameid
	 *          Das Board
	 * @param date
	 *          Der aktuelle Zeitpunkt des Wurfes
	 * @return List<Event> Die Liste aller Events fuer diesen Wurf
	 * 
	 */
	private List<Event> retrieveEventList(Pawn pawn, String gameid, Date date) {
		return new ArrayList<>();
	}

	public void setNeededServices(Map<String, Service> neededServices) {
		this.neededServices = neededServices;
	}

	public Map<String, Service> getNeededServices(String type) {
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
