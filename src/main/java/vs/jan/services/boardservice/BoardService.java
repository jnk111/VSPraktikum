package vs.jan.services.boardservice;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import vs.jan.exception.InvalidInputException;
import vs.jan.exception.InvalidPlaceIDException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.ResponseCodeException;
import vs.jan.helper.boardservice.BoardHelper;
import vs.jan.helper.events.EventTypes;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONBoardList;
import vs.jan.json.boardservice.JSONEvent;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.boardservice.JSONField;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPawnList;
import vs.jan.json.boardservice.JSONPlace;
import vs.jan.json.boardservice.JSONPlayersList;
import vs.jan.json.boardservice.JSONPlayersListElement;
import vs.jan.json.boardservice.JSONThrowsList;
import vs.jan.json.boardservice.JSONThrowsURI;
import vs.jan.json.decksservice.JSONCard;
import vs.jan.model.ServiceList;
import vs.jan.model.User;
import vs.jan.model.boardservice.Board;
import vs.jan.model.boardservice.Field;
import vs.jan.model.boardservice.Pawn;
import vs.jan.model.boardservice.Place;
import vs.jan.model.boardservice.Player;
import vs.jan.model.decksservice.ChanceCard;
import vs.jan.model.decksservice.CommCard;
import vs.jan.model.exception.Error;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.tools.HttpService;
import vs.jan.validator.BoardValidator;
import vs.jonas.services.model.Dice;

public class BoardService {

	private static final int GO_VALUE = 3000;
	private final String []DECK_TYPES = {"chance", "community"};

	private final Gson GSON = new Gson();

	/*
	 * Mapping Board -> GameUri
	 */
	private Map<Board, JSONGameURI> boards;

	private BoardValidator validator;
	private BoardHelper helper;
	private ServiceList services;
	private Map<Pawn, User> users;

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
		this.helper = new BoardHelper(null);
		this.users = new HashMap<>();
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
	 *          Client-IP von der das Board erstellt wird
	 * @throws InvalidInputException
	 *           Json-Format der Uri ungueltig
	 * @throws ResponseCodeException
	 */
	public synchronized void createNewBoard(JSONGameURI game, String host)
			throws InvalidInputException, ResponseCodeException {
		validator.checkJsonIsValid(game, Error.JSON_GAME_URI.getMsg());
		String gameId = helper.getID(game.getURI());
		String boardUri = "/boards/" + gameId;
		Board b = new Board(boardUri);
		boards.put(b, game);
		this.services = ServiceAllocator.initServices(host, gameId);
		helper.setServices(this.services);
		HttpService.post(this.services.getBroker(), game, HttpURLConnection.HTTP_OK);
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		Board b = helper.getBoard(this.boards, gameid);
		validator.checkBoardHasFields(b);
		validator.checkJsonIsValid(pawn, Error.JSON_PAWN.getMsg());
		
		Pawn p = new Pawn();
		String pawnUri = helper.getPawnUri(b, pawn.getPlayer());
		p.setPawnUri(pawnUri);
		p.setMovesUri(p.getPawnUri() + "/move");
		p.setPlayerUri(pawn.getPlayer()); // Annahme required
		p.setPlaceUri("/boards/" + gameid + "/places/" + 0);
		p.setRollsUri(p.getPawnUri() + "/roll");
		b.addNewPawn(p);
		String playerUri = this.services.getGames().replace("/games", "") + p.getPlayerUri();
//		System.out.println(playerUri);
//		Player player = helper.getPlayer(playerUri, gameid);

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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(placeid, Error.PLACE_ID.getMsg());
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
	 * @throws ResponseCodeException
	 */
	public synchronized void movePawn(String gameid, String pawnid, int rollValue)
			throws ResourceNotFoundException, InvalidInputException, ResponseCodeException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		Board b = helper.getBoard(this.boards, gameid);
		Pawn p = helper.getPawn(b, pawnid);
		int oldPos = p.getPosition(); // alte Position der Figur
		int newPos = oldPos + rollValue; // Neue Position der Figur

		// Eine Runde rumgelaufen?
		if (newPos >= b.getFields().size() - 1) {
			newPos = ((newPos % b.getFields().size()));
			payRunOverGoValue(p, gameid);
		}

		b.getFields().get(oldPos).removePawn(p); // Figur von alter
																							// Position entfernen
		p.setPosition(newPos); // setze neue Pos-Nr.
		b.getFields().get(newPos).addPawn(p); // Setze Figur auf neue
																					// Position
		b.updatePositions(oldPos, newPos); // markiere, dass auf Position
																				// 'newPos' Figuren stehen
		p.updatePlaceUri(newPos); // Update Placeuri to the new Place
		String reas = p.getPlayerUri() + " has moved the pawn: " + p.getPawnUri() + " to: " + p.getPlaceUri();
		String resource = p.getRollsUri();
		JSONEvent event = new JSONEvent(gameid, EventTypes.MOVE_PAWN.getType(), EventTypes.MOVE_PAWN.getType(), reas, resource, p.getPlayerUri());
		helper.postEvent(event, this.services.getEvents());
		Place place = helper.getPlace(b.getFields(), String.valueOf(newPos));

		if (place.isPlace() && !place.isJail()) {
			String uri = this.services.getBroker() + "/" + gameid + "/places/" + newPos + "/visit/" + pawnid;
			HttpService.post(uri, p.getPlayerUri(), HttpURLConnection.HTTP_OK);
			
		} else if (place.isJail()){
			moveToJail(gameid, b, p, newPos);
			
		} else if (place.isChance()){
			doFurtherDecksActions(gameid, newPos, b, p, DECK_TYPES[0]);
			
		} else if (place.isCommunity()) {
			doFurtherDecksActions(gameid, newPos, b, p, DECK_TYPES[1]);
		}
	}


	private void payRunOverGoValue(Pawn p, String gameid) {
		String toId = helper.getID(p.getPawnUri());
		String bankUri = this.services.getBank() + "/" + gameid + "/transfer/to/" + toId + "/" + GO_VALUE;
		String reason = "Player with id: " + toId + " receives money from the bank by running over Go";
		HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
		JSONEvent event = new JSONEvent(gameid, EventTypes.MOVED_OVER_GO.getType(), EventTypes.MOVED_OVER_GO.getType(), reason, p.getRollsUri(), p.getPlayerUri());
		helper.postEvent(event, this.services.getEvents());
	}

	private void moveToJail(String gameid, Board board, Pawn pawn, int newPos) {
		
		int jailPos = Place.InJail.ordinal();
		board.getFields().get(newPos).removePawn(pawn);
		pawn.setPosition(jailPos);
		board.getFields().get(jailPos).addPawn(pawn);
		board.updatePositions(newPos, jailPos);
		pawn.updatePlaceUri(jailPos);
		String reas = pawn.getPlayerUri() + " has moved to jail: " + pawn.getPawnUri() + " to: " + pawn.getPlaceUri();
		JSONEvent event = new JSONEvent(gameid, EventTypes.MOVED_TO_JAIL.getType(), EventTypes.MOVED_TO_JAIL.getType(), reas, pawn.getRollsUri(), pawn.getPlayerUri());
		helper.postEvent(event, this.services.getEvents());
	}

	private void doFurtherDecksActions(String gameid, int newPos, Board board, Pawn pawn, String type) {
		
		// String url = this.services.getDecks() + "/" + gameid + "/" + type;
		
		// Temp
		String url = "http://localhost:4567/decks" + "/" + gameid + "/" + type;
		String json = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONCard card = GSON.fromJson(json, JSONCard.class);
		String name = card.getName();
		
		if (name.equals(ChanceCard.GO_TO_JAIL.getName())) {
			moveToJail(gameid, board, pawn, newPos);
			
		} else if (name.equals(ChanceCard.MOVE_3_TIMES.getName())) {
			movePawn(gameid, helper.getID(pawn.getPawnUri()), 3);
			
		} else if (name.equals(ChanceCard.MOVE_TO_GO.getName())) {
			movePawn(gameid, helper.getID(pawn.getPawnUri()), (board.getFields().size() - 1) - pawn.getPosition());
			
		} else if (name.equals(CommCard.GET_MONEY_FROM_ALL_PLAYERS.getName())) {
			
			getMoneyFromAllPlayers(pawn, gameid);
			
		} else if (name.equals(CommCard.GET_MONEY_FROM_BANK.getName())) {
			getMoneyFromBank(pawn, gameid);
		}		
	}

	private void getMoneyFromBank(Pawn pawn, String gameid) {
		String toId = helper.getID(pawn.getPlayerUri());
		
		// Temp
		String bankUri = this.services.getBank() + "/" + gameid + "/transfer/to/" + toId + "/" + CommCard.BANK_MONEY;
		HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
		
		String reason = "Player with id: " + toId + " got a community card and receives money from the bank";
		JSONEvent event = new JSONEvent(gameid, EventTypes.GOT_MONEY_FROM_BANK.getType(), EventTypes.GOT_MONEY_FROM_BANK.getType(), reason, pawn.getRollsUri(), pawn.getPlayerUri());
		helper.postEvent(event, this.services.getEvents());
		
	}

	public void getMoneyFromAllPlayers(Pawn pawn, String gameid) {
		
		String toId = helper.getID(pawn.getPlayerUri());
		String url = this.services.getGames() + "/" + gameid + "/players"; 
		String players = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONPlayersList list = GSON.fromJson(players, JSONPlayersList.class);
		
		for(JSONPlayersListElement elem: list.getPlayers()) {
			String fromId = helper.getID(elem.getId());			
			
			// TODO: saldocheck
			
			// Temp
			String bankUri = this.services.getBank() + "/" + gameid + "/transfer/from/" + fromId + "/to/" + toId + "/" + CommCard.PLAYER_MONEY;			
			HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
		}
		String reason = "Player with id: " + toId + " got a community card and receives money from all other players";
		JSONEvent event = new JSONEvent(gameid, EventTypes.GOT_MONEY_ALL_PLAYERS.getType(), EventTypes.GOT_MONEY_ALL_PLAYERS.getType(), reason, pawn.getRollsUri(), pawn.getPlayerUri());
		helper.postEvent(event, this.services.getEvents());
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
	 * @throws ResponseCodeException
	 * 
	 */
	public synchronized JSONEventList rollDice(String gameid, String pawnid)
			throws ResourceNotFoundException, ResponseCodeException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());

		// Temp
		validator.checkPlayerHasMutex(gameid, pawnid, this.services.getGame(), true);

		Board board = helper.getBoard(this.boards, gameid);
		Pawn pawn = helper.getPawn(board, pawnid);
		Dice roll = rollDice(pawn, gameid); // Zum Testen Local
		movePawn(gameid, pawnid, roll.getNumber());
		helper.addThrow(this.throwMap, pawn, roll);

		return helper.receiveEventList(this.services.getEvents(), pawn.getPlayerUri(), gameid, new Date());
	}

	/**
	 * gemachten Wurd in die Wurfliste, die zu der Figur gehoert hinzu
	 * 
	 * @param pawn
	 *          Die Figur, fuer die gewuerfelt wird
	 * @param gameid
	 *          Die ID des Games
	 * @return Der Int-Wert des gemachten Wurfes
	 * @throws ResponseCodeException
	 */
	private Dice rollDice(Pawn pawn, String gameid) throws ResponseCodeException {

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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkJsonIsValid(place, Error.JSON_PLACE.getMsg());
		Board key = helper.getBoard(this.boards, gameid);
		Place p = helper.getPlace(key.getFields(), helper.getID(pathinfo));
		p.setBrokerUri(place.getBroker());
		p.setName(place.getName());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkJsonIsValid(board, Error.JSON_BOARD.getMsg());
		Board b = helper.getBoard(this.boards, gameid);

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
			String brokerUri = "/broker/" + gameid + "/places/" + i;
			p.setBrokerUri(brokerUri);
			p.setPlaceUri(placeUri);
			Field f = new Field(p);
			fields.add(f);
			HttpService.put(this.services.getBroker().replace("/broker", "") + p.getBrokerUri(), p.convertToBrokerPlace(), HttpURLConnection.HTTP_OK);
			
		}

		key.setFields(fields);
		key.setPlayers("/games/" + gameid + "/players");
		
		initDecks(key);
	}
	

	private void initDecks(Board key) {
		
		JSONGameURI uri = this.boards.get(key);
		// Workaround
		String url = "http://localhost:4567/decks";
		HttpService.post(url, uri, HttpURLConnection.HTTP_OK);
		// HttpService.post(this.services.getDecks(), uri, HttpURLConnection.HTTP_CREATED);
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
				validator.checkJsonIsValid(json, Error.JSON_PAWN.getMsg());
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkJsonIsValid(pawn, Error.JSON_PAWN.getMsg());
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
