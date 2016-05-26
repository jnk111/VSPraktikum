package vs.jan.services.boardservice;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.exception.InvalidInputException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.helper.boardservice.BoardHelper;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONBoardList;
import vs.jan.json.boardservice.JSONField;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPawnList;
import vs.jan.json.boardservice.JSONPlace;
import vs.jan.json.boardservice.JSONThrowsList;
import vs.jan.json.boardservice.JSONThrowsURI;
import vs.jan.model.ServiceList;
import vs.jan.model.boardservice.Board;
import vs.jan.model.boardservice.Field;
import vs.jan.model.boardservice.Pawn;
import vs.jan.model.boardservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.InvalidPlaceIDException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.tools.HttpService;
import vs.jan.validator.boardservice.BoardValidator;
import vs.jonas.services.model.Dice;
import vs.jonas.services.model.Event;

public class BoardService {

	private final Gson GSON = new Gson();
	
	
	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;

	private BoardValidator validator;
	private BoardHelper helper;
	private ServiceList services;

	/*
	 * Uri-Liste der gemachten Wuerfe JSONThrowsUri -> die URI der von einer Pawn
	 * gemachten Wuerfe JSONThrowsList -> die Werte der Wuerfel
	 */
	private Map<JSONThrowsURI, JSONThrowsList> throwMap;

	/**
	 * Defaultkonstruktor
	 */
	public BoardService() {

		boards = new HashMap<>();
		throwMap = new HashMap<>();
		this.validator = new BoardValidator();
		this.helper = new BoardHelper();
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
	 * @param host 
	 * @param host
	 * 					Client-IP von der das Board erstellt wird
	 * @throws InvalidInputException
	 *           Json-Format der Uri ungueltig
	 */
	public synchronized void createNewBoard(JSONGameURI game, String host) throws InvalidInputException {
		validator.checkGameIsValid(game);
		String gameId = helper.getID(game.getURI());
		String boardUri = "/boards/" + gameId;
		Board b = new Board(boardUri);
		boards.put(b, game);
		this.services = ServiceAllocator.initServices(host, gameId);
		placeABoard(gameId, b.convert());
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
		validator.checkBoardHasFields(b);
		validator.checkPawnInputIsValid(pawn);
		Pawn p = new Pawn();
		String pawnUri = helper.getPawnUri(b, pawn.getPlayer());
		p.setPawnUri(pawnUri);
		p.setMovesUri(p.getPawnUri() + "/move");
		p.setPlayerUri(pawn.getPlayer()); // Annahme required
		p.setPlaceUri("/boards/" + gameid + "/places/" + 0);
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
		validator.checkPawnIdIsNotNull(pawnid);
		Board b = helper.getBoard(this.boards, gameid);
		Pawn p = helper.getPawn(b, pawnid);
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

		for (JSONThrowsURI uri : throwMap.keySet()) {
			if (uri.getRollUri().contains(pawnid)) {
				return throwMap.get(uri);
			}
		}
		throw new ResourceNotFoundException();
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
		validator.checkPlaceIdIsNotNull(placeid);
		Board b = helper.getBoard(this.boards, gameid);
		Place p = helper.getPlace(b.getFields(), placeid);
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
		Board b = helper.getBoard(this.boards, gameid);
		Pawn p = helper.getPawn(b, pawnid);
		int oldPos = p.getPosition(); // alte Position der Figur
		int newPos = oldPos + rollValue; // Neue Position der Figur

		// Eine Runde rumgelaufen?
		if (newPos >= b.getFields().size() - 1) {
			newPos = ((newPos % b.getFields().size()));
		}

		b.getFields().get(oldPos).removePawn(p); // Figur von alter
																							// Position entfernen
		p.setPosition(newPos); // setze neue Pos-Nr.
		b.getFields().get(newPos).addPawn(p); // Setze Figur auf neue
																					// Position
		b.updatePositions(oldPos, newPos); // markiere, dass auf Position
																				// 'newPos' Figuren stehen
		p.updatePlaceUri(newPos); // Update Placeuri to the new Place
		helper.postEvent(gameid, "move", "move", p, this.services.getEvents());
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
		
		// Temp
		validator.checkPlayerHasMutex(gameid, pawnid, "http://localhost:4567/games");
		
		Board board = helper.getBoard(this.boards, gameid);
		Pawn pawn = helper.getPawn(board, pawnid);
		Dice roll = rollDice(pawn, gameid); // Zum Testen Local
		movePawn(gameid, pawnid, roll.getNumber());
		helper.addThrow(this.throwMap, pawn, roll);

		return helper.retrieveEventList(pawn, gameid, new Date());
	}

	/**
	 * gemachten Wurd in die Wurfliste, die zu der Figur gehoert hinzu
	 * 
	 * @param pawn
	 *          Die Figur, fuer die gewuerfelt wird
	 * @param gameid
	 *          Die ID des Games
	 * @return Der Int-Wert des gemachten Wurfes
	 */
	private Dice rollDice(Pawn pawn, String gameid) {
		System.out.println("DICE URL: " + this.services.getDice());
		String url = this.services.getDice() + "?" + "player=" + pawn.getPlayerUri() + "&game=" + gameid;
		
		String json = HttpService.get(this.services.getDice() + "?" + "player=" + pawn.getPlayerUri() + "&game=" + gameid,
				HttpURLConnection.HTTP_OK);
		Dice roll = GSON.fromJson(json, Dice.class);
		validator.checkRollValueIsValid(roll);
		return roll;
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
		Pawn p = helper.getPawn(b, pawnid);
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
	public synchronized void updateAPlaceOnTheBoard(JSONPlace place, String pathinfo, String gameid) {
		validator.checkGameIdIsNotNull(gameid);
		validator.checkPlaceInputIsValid(place);
		// Board key = helper.getBoard(this.boards, gameid);
		// Place p = helper.getPlace(key.getFields(), helper.getID(pathinfo));
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
		validator.checkBoardIsValid(board);
		Board b = helper.getBoard(this.boards, gameid);
		//this.services = ServiceAllocator.initServices("localhost:4567", gameid);
		if (!b.hasFields()) {
			initNewBoard(b, gameid);
		} else {
			updateBoard(b, board, gameid);
		}
	}

	private void initNewBoard(Board key, String gameid) {
		List<Field> fields = new ArrayList<>();

		for (int i = 0; i < Place.values().length; i++) {
			Place p = Place.values()[i];
			String placeUri = "/boards/" + gameid + "/places/" + i;
			p.setPlaceUri(placeUri);
			Field f = new Field(p);
			fields.add(f);
		}

		key.setFields(fields);
		key.setPlayers("/games/" + gameid + "/players");
	}

	private void updateBoard(Board key, JSONBoard board, String gameid) {
		List<String> placeUris = new ArrayList<>();
		board.getFields().forEach(f -> placeUris.add(f.getPlace()));
		for (int i = 0; i < board.getFields().size(); i++) {

			JSONField field = board.getFields().get(i);
			int placeNum = -1;

			try {
				placeNum = Integer.parseInt(helper.getID(field.getPlace()));
			} catch (NumberFormatException e) {
				throw new InvalidPlaceIDException(Error.PLACE_ID_NUM.getMsg());
			}
			;

			Place p = Place.values()[placeNum];
			Field f = helper.getField(key, p.getPlaceUri());
			f.setPawns(new ArrayList<>());

			for (String pawnUri : field.getPawns()) {
				Pawn pawn = helper.getPawn(key, pawnUri);
				pawn.setPlaceUri(field.getPlace());
				pawn.setMovesUri(pawnUri + "/move");
				pawn.setPosition(placeNum);
				pawn.setRollsUri(pawnUri + "/roll");
				JSONPawn json = pawn.convert();
				validator.checkPawnInputIsValid(json);
				f.addPawn(pawn);
			}

			// Alle Fields loeschen die nicht im JSON waren
			key.getFields().removeIf(f2 -> !placeUris.contains(f2.getPlace().getPlaceUri()));
			key.reloadPositions();
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
		validator.checkPawnInputIsValid(pawn);
		validator.checkGameIdIsNotNull(gameid);
		Board b = helper.getBoard(this.boards, gameid);
		Pawn p = helper.getPawn(b, pawn.getId());

		p.setMovesUri(pawn.getMove());
		p.setPawnUri(pawn.getId());
		p.setPlaceUri(pawn.getPlace());
		p.setPlayerUri(pawn.getPlayer());
		p.setPosition(pawn.getPosition());
		p.setRollsUri(pawn.getRoll());
	}


	public Map<Board, JSONGameURI> getBoards() {

		return this.boards;
	}

}
