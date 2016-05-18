package vs.jan.services.boardservice;

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

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.ExcMessageHandler;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.MutexPutException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.TurnMutexNotFreeException;
import vs.jan.helper.boardservice.BoardServiceHelper;
import vs.jan.json.JSONBoard;
import vs.jan.json.JSONBoardList;
import vs.jan.json.JSONField;
import vs.jan.json.JSONGameURI;
import vs.jan.json.JSONPawn;
import vs.jan.json.JSONPawnList;
import vs.jan.json.JSONPlace;
import vs.jan.json.JSONThrowsList;
import vs.jan.json.JSONThrowsURI;
import vs.jan.model.Board;
import vs.jan.model.Field;
import vs.jan.model.Pawn;
import vs.jan.model.Place;
import vs.jan.model.Service;
import vs.jan.model.ServiceNames;
import vs.jan.model.User;
import vs.jan.tools.HttpService;
import vs.jan.validator.boardservice.BoardServiceValidator;
import vs.jonas.services.model.Dice;
import vs.jonas.services.model.Event;

public class BoardService {

	private final Gson GSON = new Gson();
	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;
	private BoardServiceValidator validator;
	private BoardServiceHelper helper;

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
		this.validator = new BoardServiceValidator();
		this.helper = new BoardServiceHelper();
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

		validator.checkGameIsValid(game);
		String boardUri = "/boards/" + game.getURI().split("/")[2];
		boards.put(new Board(boardUri), game);
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
		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		return b.convert();
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

		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		validator.checkBoardHasFields(b, gameid);
		validator.checkPawnInputIsValid(pawn);

		Pawn p = new Pawn();
		String pawnUri = helper.getPawnUri(b, pawn.getPlayer());
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

		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		JSONPawnList pl = new JSONPawnList();
		for (Field f : b.getFields()) {
			for (Pawn p : f.getPawns()) {
				pl.addPawnURI(p.getPawnUri());
			}
		}
		return pl;
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
		validator.checkGameIdIsNotNull(gameid);
		validator.checkPawnIsNotNull(pawnid, gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		Pawn p = helper.getPawn(b, pawnid);
		validator.checkPawnIsNotNull(pawnid, gameid);
		return p.convert();
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

		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);

		for (JSONThrowsURI uri : throwMap.keySet()) {
			if (uri.getRollUri().contains(pawnid)) {
				return throwMap.get(uri);
			}
		}

		throw new ResourceNotFoundException(ExcMessageHandler.getPawnNotFoundMsg(pawnid, gameid));
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

		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);

		List<String> allPlaceURIs = new ArrayList<>();
		for (Field f : b.getFields()) {
			allPlaceURIs.add(f.getPlace().getPlaceUri());
		}
		return allPlaceURIs;
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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkPlaceIdIsNotNull(placeid, gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		Place p = helper.getPlace(b.getFields(), placeid);
		validator.checkPlaceIsNotNull(p, placeid, gameid);
		return p.convert();
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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkPawnIdIsNotNull(pawnid);
		validator.checkRollValueIsValid(rollValue);
		Board b = helper.getBoard(this.boards, gameid);

		validator.checkBoardIsNotNull(b, gameid);

		Pawn p = helper.getPawn(b, pawnid);

		validator.checkPawnIsNotNull(pawnid, gameid);

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
		helper.postEvent(gameid, "move", "move", p, this.neededServices);
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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkPawnIdIsNotNull(pawnid);
		Board board = helper.getBoard(this.boards, gameid);
		Pawn pawn = helper.getPawn(board, pawnid);
		validator.checkBoardIsNotNull(board, gameid);
		validator.checkPawnIsNotNull(pawnid, gameid);

		int rollValue = doDiceRollLocal(pawn, gameid); // Zum Testen Local
		movePawn(gameid, pawnid, rollValue);

		// placeAPawnRESTCall(gameid, pawn);

		// weitere Aktionen...

		return helper.retrieveEventList(pawn, gameid, new Date());

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
		pawn.setPlayerUri(playerUri);

		@SuppressWarnings("unused")
		User user = getPlayer(pawn, gameid);
		String json = HttpService.get("http://localhost:4567/dice?" + "player=" + pawn.getPlayerUri() + "&game=" + gameid,
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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkPawnIdIsNotNull(pawnid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		Pawn p = helper.getPawn(b, pawnid);
		validator.checkPawnIsNotNull(pawnid, gameid);
		b.removePawn(p);
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
		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		boards.remove(b);
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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkPlaceIsValid(place);

		Board key = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(key, gameid);

		Place p = helper.getPlaceWithPathInfo(place, key.getFields(), pathinfo);
		validator.checkPlaceIsNotNull(p, pathinfo, gameid);

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

		validator.checkGameIdIsNotNull(gameid);
		validator.checkBoardIsValid(gameid, board);
		Board key = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(key, gameid);
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
				JSONPawn p = pawn.convert();
				validator.checkPawnIsValid(p);
				pawns.add(pawn);
			}

			Place place = new Place();
			place.setPlaceUri(f.getPlace());
			field.setPawns(pawns);
			field.setPlace(place);
			validator.checkPlaceIsValid(place.convert());
			fields.add(field);
		}

		key.setFields(fields);
		key.setPlayers("/boards/" + gameid + "/players");
		key.setPositions(board.getPositions()); // TODO: nachfragen

		JSONGameURI entry = boards.get(key);
		boards.put(key, entry);
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

		validator.checkPawnInputIsValid(pawn);
		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardIsNotNull(b, gameid);
		Pawn p = helper.getPawn(b, pawn.getId());
		validator.checkPawnIsNotNull(pawn.getId(), gameid);

		p.setMovesUri(pawn.getMove());
		p.setPawnUri(pawn.getId());
		p.setPlaceUri(pawn.getPlace());
		p.setPlayerUri(pawn.getPlayer());
		p.setPosition(pawn.getPosition());
		p.setRollsUri(pawn.getRoll());
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

	/*
	 * ===========================================================================
	 * TODO: implemetieren folgende Funktionionen
	 * ===========================================================================
	 */
	/**
	 * TODO: implement REST-Aufruf um einer Figur ein neues Feld zuzuweisen,
	 * nachdem sie bewegt wurde wird von rollDice() aufgerufen
	 * 
	 * @param gameid
	 * @param pawn
	 */
	@SuppressWarnings("unused")
	private void placeAPawnRESTCall(String gameid, Pawn pawn) {

	}

	/**
	 * TODO: implement REST-Aufruf um einer Figur eine neue Position zuzuweisen,
	 * nachdem sie bewegt wurde wird von rollDice() aufgerufen
	 * 
	 * @param gameid
	 * @param pawn
	 */
	@SuppressWarnings("unused")
	private void movePawnRESTCall(String gameid, String pawnid, int rollValue) {

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
	@SuppressWarnings("unused")
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
			throw new InvalidInputException("Given URL is not Valid!");
		} catch (IOException ioe) {
			throw new ConnectionRefusedException("No Connection to Service!");
		}

		return -1;
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
	@SuppressWarnings("unused")
	private void putPlayersTurn(Pawn pawn, String gameid)
			throws MutexPutException, InvalidInputException, ConnectionRefusedException {
		HttpService.put("http://localhost:4567/games/" + gameid + "/turn", pawn, HttpURLConnection.HTTP_OK);
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
	@SuppressWarnings("unused")
	private synchronized void checkPlayerHasTurn(Pawn pawn, String gameid)
			throws TurnMutexNotFreeException, ConnectionRefusedException, InvalidInputException {

		String json = HttpService.get("http://localhost:4567/games/" + gameid + "/turn", HttpURLConnection.HTTP_OK);
		User currPlayer = GSON.fromJson(json, User.class);

		if (currPlayer != null && currPlayer.isValid()) {
			throw new TurnMutexNotFreeException("Player: " + currPlayer + " does not have the Mutex!");
		}
	}
	
	/* 
	 * ===========================================================================
	 * TODO: Spaeter entfernen
	 * * ===========================================================================
	 */

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
	
/*
 * ===========================================================================
 * Default Getter
 * ===========================================================================
 */

	public Map<Board, JSONGameURI> getBoards() {

		return this.boards;
	}

}
