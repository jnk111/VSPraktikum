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
import vs.jan.model.boardservice.Board;
import vs.jan.model.boardservice.Field;
import vs.jan.model.boardservice.Pawn;
import vs.jan.model.boardservice.Place;
import vs.jan.model.decksservice.ChanceCard;
import vs.jan.model.decksservice.CommCard;
import vs.jan.model.exception.Error;
import vs.jan.model.exception.PlayerHasAlreadyRolledException;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.services.allocator.ServiceAllocator;
import vs.jan.tools.HttpService;
import vs.jan.validator.BoardValidator;
import vs.jonas.services.model.Dice;

public class BoardService {

	private final int GO_VALUE = 3000;
	private final String[] DECK_TYPES = { "chance", "community" };
	private final String BOARDS_PREFIX = "/boards/";
	private final String MOVE_SUFFIX = "/move";
	private final String ROLL_SUFFIX = "/roll";
	private final String SERVICE_SUFFIX = "/services";
	private final String PLACES_INFIX = "/places/";
	private final String TRANSER_TO_INFIX = "/transfer/to/";
	private final String TO_INFIX = "/to/";
	private final String TRANSER_FROM_INFIX = "/transfer/from/";
	private final String BROKER_PREFIX = "/broker/";
	private final String VISIT_INFIX = "/visit/";
	private final String GAMES_PREFIX = "/games/";
	private final String HTTP_PREFIX = "http://";
	private final String PLAYERS_SUFFIX = "/players";
	private final String SERVICE_QPARAM = "?services=";

	private final Gson GSON = new Gson();

	private Map<String, Board> boards;

	private BoardValidator validator;
	private ServiceList services;

	private Map<JSONThrowsURI, JSONThrowsList> throwMap;
	private Pawn currPlayer;

	/**
	 * Defaultkonstruktor
	 */
	public BoardService() {

		this.boards = new HashMap<>();
		this.throwMap = new HashMap<>();
		this.validator = new BoardValidator();
		this.currPlayer = null;
	}

	/**
	 * Liefert alle Board-Uris, die dem Spiel zugeteilt wurden
	 * 
	 * @return Liste der Board-Uris als JSON-DTO
	 */
	public JSONBoardList getAllBoardURIs() {
		JSONBoardList boardURIs = new JSONBoardList();
		boards.forEach((k, v) -> boardURIs.addBoardURI(v.getUri()));
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

		String gameId = BoardHelper.getID(game.getURI());
		String gsServiceUri = HTTP_PREFIX + host + GAMES_PREFIX + gameId + SERVICE_SUFFIX;
		String boardUri = BOARDS_PREFIX + gameId;
		Board b = new Board(boardUri);
		boards.put(BoardHelper.getID(game.getURI()), b);
		this.services = ServiceAllocator.initServices(gsServiceUri, gameId);
		BoardHelper.setServices(this.services);
		String queryParam = gsServiceUri;
		String serviceUri = this.services.getBroker() + SERVICE_QPARAM + queryParam;
		HttpService.post(serviceUri, game, HttpURLConnection.HTTP_OK);
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

		Board b = BoardHelper.getBoard(boards, gameid);
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
		Board b = BoardHelper.getBoard(boards, gameid);
		validator.checkBoardHasFields(b);
		validator.checkJsonIsValid(pawn, Error.JSON_PAWN.getMsg());

		Pawn p = new Pawn();
		String pawnUri = BoardHelper.getPawnUri(b, pawn.getPlayer());
		p.setPawnUri(pawnUri);
		p.setMovesUri(p.getPawnUri() + MOVE_SUFFIX);
		p.setPlayerUri(pawn.getPlayer()); // Annahme required
		p.setPlaceUri(BOARDS_PREFIX + gameid + PLACES_INFIX + 0);
		p.setRollsUri(p.getPawnUri() + ROLL_SUFFIX);
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
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());

		Board b = BoardHelper.getBoard(boards, gameid);
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

		Board b = BoardHelper.getBoard(boards, gameid);
		Pawn p = BoardHelper.getPawn(b, pawnid);

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
		Board b = BoardHelper.getBoard(boards, gameid);
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

		Board b = BoardHelper.getBoard(boards, gameid);
		Place p = BoardHelper.getPlace(b.getFields(), placeid);

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
	 * @throws TransactionFailedException
	 */
	public synchronized void movePawn(String gameid, String pawnid, int rollValue)
			throws ResourceNotFoundException, InvalidInputException, ResponseCodeException, TransactionFailedException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());

		Board b = BoardHelper.getBoard(boards, gameid);
		Pawn p = BoardHelper.getPawn(b, pawnid);
		int oldPos = p.getPosition(); // alte Position der Figur
		int newPos = oldPos + rollValue; // Neue Position der Figur

		// Eine Runde rumgelaufen?
		if (newPos >= b.getFields().size()) {
			newPos = ((newPos % b.getFields().size()));
			payRunOverGoValue(p, gameid);
		}

		b.getFields().get(oldPos).removePawn(p);
		p.setPosition(newPos);
		b.getFields().get(newPos).addPawn(p);
		b.updatePositions(oldPos, newPos);
		p.updatePlaceUri(newPos);

		String resource = p.getRollsUri();
		String type = EventTypes.MOVE_PAWN.getType();
		JSONEvent event = new JSONEvent(gameid, type, type, type, resource, p.getPlayerUri());

		BoardHelper.broadCastEvent(event);
		BoardHelper.postEvent(event);

		Place place = BoardHelper.getPlace(b.getFields(), String.valueOf(newPos));

		if (place.isPlace() && !place.isJail()) {
			String uri = this.services.getBroker() + "/" + gameid + PLACES_INFIX + newPos + VISIT_INFIX + pawnid;
			HttpService.post(uri, p.getPlayerUri(), HttpURLConnection.HTTP_OK);

		} else if (place.isJail()) {
			moveToJail(gameid, b, p, newPos);

		} else if (place.isChance()) {
			doFurtherDecksActions(gameid, newPos, b, p, DECK_TYPES[0]);

		} else if (place.isCommunity()) {
			doFurtherDecksActions(gameid, newPos, b, p, DECK_TYPES[1]);

		} else if (place.isTax()) {
			payTax(p, gameid);
		}
	}

	private void payTax(Pawn p, String gameid) throws TransactionFailedException {
		String fromId = BoardHelper.getID(p.getPawnUri());
		String bankUri = this.services.getBank() + "/" + gameid + TRANSER_FROM_INFIX + fromId + "/"
				+ Place.EinkStr.getPrice();
		JSONEvent event = null;
		String type = null;

		try {
			HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
			type = EventTypes.PAYED_TAX.getType();
			event = new JSONEvent(gameid, type, type, type, p.getRollsUri(), p.getPlayerUri());

		} catch (Exception e) {
			type = EventTypes.CANNOT_PAY_TAX.getType();
			event = new JSONEvent(gameid, type, type, type, p.getRollsUri(), p.getPlayerUri());
			throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());

		} finally {
			BoardHelper.postEvent(event);
			BoardHelper.broadCastEvent(event);

		}
	}

	private void payRunOverGoValue(Pawn p, String gameid) {
		String toId = BoardHelper.getID(p.getPawnUri());
		String bankUri = this.services.getBank() + "/" + gameid + TRANSER_TO_INFIX + toId + "/" + GO_VALUE;

		HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
		String type = EventTypes.MOVED_OVER_GO.getType();
		JSONEvent event = new JSONEvent(gameid, type, type, type, p.getRollsUri(), p.getPlayerUri());

		BoardHelper.broadCastEvent(event);
		BoardHelper.postEvent(event);
	}

	private void moveToJail(String gameid, Board board, Pawn pawn, int newPos) {
		int jailPos = Place.InJail.ordinal();
		board.getFields().get(newPos).removePawn(pawn);
		pawn.setPosition(jailPos);
		board.getFields().get(jailPos).addPawn(pawn);
		board.updatePositions(newPos, jailPos);
		pawn.updatePlaceUri(jailPos);

		String type = EventTypes.MOVED_TO_JAIL.getType();
		JSONEvent event = new JSONEvent(gameid, type, type, type, pawn.getRollsUri(), pawn.getPlayerUri());

		BoardHelper.broadCastEvent(event);
		BoardHelper.postEvent(event);
	}

	private void doFurtherDecksActions(String gameid, int newPos, Board board, Pawn pawn, String type)
			throws TransactionFailedException {

		String url = this.services.getDecks() + "/" + gameid + "/" + type;
		String json = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONCard card = GSON.fromJson(json, JSONCard.class);
		String name = card.getName();

		if (name.equals(ChanceCard.GO_TO_JAIL.getName())) {
			moveToJail(gameid, board, pawn, newPos);

		} else if (name.equals(ChanceCard.MOVE_3_TIMES.getName())) {
			movePawn(gameid, BoardHelper.getID(pawn.getPawnUri()), 3);

		} else if (name.equals(ChanceCard.MOVE_TO_GO.getName())) {
			movePawn(gameid, BoardHelper.getID(pawn.getPawnUri()), (board.getFields().size() - 1) - pawn.getPosition());

		} else if (name.equals(CommCard.GET_MONEY_FROM_ALL_PLAYERS.getName())) {

			getMoneyFromAllPlayers(pawn, gameid);

		} else if (name.equals(CommCard.GET_MONEY_FROM_BANK.getName())) {
			getMoneyFromBank(pawn, gameid);
		}
	}

	private void getMoneyFromBank(Pawn pawn, String gameid) throws TransactionFailedException {
		String toId = BoardHelper.getID(pawn.getPlayerUri());
		String bankUri = this.services.getBank() + "/" + gameid + TRANSER_TO_INFIX + toId + "/" + CommCard.BANK_MONEY;
		JSONEvent event = null;

		try {

			HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);

			String type = EventTypes.GOT_MONEY_FROM_BANK.getType();
			event = new JSONEvent(gameid, type, type, type, pawn.getRollsUri(), pawn.getPlayerUri());

			BoardHelper.broadCastEvent(event);
			BoardHelper.postEvent(event);

		} catch (Exception e) {
			throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());
		}
	}

	public void getMoneyFromAllPlayers(Pawn pawn, String gameid) throws TransactionFailedException {
		String toId = BoardHelper.getID(pawn.getPlayerUri());
		String url = this.services.getGames() + "/" + gameid + PLAYERS_SUFFIX;
		String players = HttpService.get(url, HttpURLConnection.HTTP_OK);
		JSONPlayersList list = GSON.fromJson(players, JSONPlayersList.class);
		JSONEvent event = null;

		for (JSONPlayersListElement elem : list.getPlayers()) {
			String fromId = BoardHelper.getID(elem.getId());
			String selfId = BoardHelper.getID(pawn.getPlayerUri());

			try {
				if (!fromId.equals(selfId)) {

					String bankUri = this.services.getBank() + "/" + gameid + TRANSER_FROM_INFIX + fromId + TO_INFIX + toId + "/"
							+ CommCard.PLAYER_MONEY;
					HttpService.post(bankUri, null, HttpURLConnection.HTTP_CREATED);
					String type = EventTypes.GOT_MONEY_ALL_PLAYERS.getType();
					event = new JSONEvent(gameid, type, type, type, pawn.getRollsUri(), pawn.getPlayerUri());

				}

			} catch (Exception e) {
				String type = EventTypes.CANNOT_PAY_MONEY_COMMUNITY.getType();
				event = new JSONEvent(gameid, type, type, type, pawn.getRollsUri(), elem.getId());
				throw new TransactionFailedException(Error.TRANS_FAIL.getMsg());

			} finally {

				BoardHelper.broadCastEvent(event);
				BoardHelper.postEvent(event);
			}

		}
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
	 * @throws TransactionFailedException
	 * @throws InvalidInputException
	 * 
	 */
	public synchronized JSONEventList rollDice(String gameid, String pawnid) throws ResourceNotFoundException,
			ResponseCodeException, InvalidInputException, TransactionFailedException, PlayerHasAlreadyRolledException {
		validator.checkIdIsNotNull(gameid, Error.GAME_ID.getMsg());
		validator.checkIdIsNotNull(pawnid, Error.PAWN_ID.getMsg());
		validator.checkPlayerHasMutex(gameid, pawnid, this.services.getGame(), false);

		Board board = BoardHelper.getBoard(boards, gameid);
		Pawn pawn = BoardHelper.getPawn(board, pawnid);

		if (this.currPlayer == null || !pawn.equals(this.currPlayer)) {

			this.currPlayer = pawn;
			Dice roll = rollDice(pawn, gameid); // Zum Testen Local
			movePawn(gameid, pawnid, roll.getNumber());
			BoardHelper.addThrow(this.throwMap, pawn, roll);

			return BoardHelper.receiveEventList(pawn.getPlayerUri(), gameid, new Date());
		}

		throw new PlayerHasAlreadyRolledException(Error.ALR_ROLLED.getMsg());
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

		Board b = BoardHelper.getBoard(boards, gameid);
		Pawn p = BoardHelper.getPawn(b, pawnid);

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

		Board b = BoardHelper.getBoard(boards, gameid);
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

		Board b = BoardHelper.getBoard(boards, gameid);
		Place p = BoardHelper.getPlace(b.getFields(), BoardHelper.getID(pathinfo));

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

		Board b = BoardHelper.getBoard(boards, gameid);

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
			String placeUri = BOARDS_PREFIX + gameid + PLACES_INFIX + i;
			String brokerUri = BROKER_PREFIX + gameid + PLACES_INFIX + i;

			p.setBrokerUri(brokerUri);
			p.setPlaceUri(placeUri);

			Field f = new Field(p);
			fields.add(f);
			HttpService.put(this.services.getBrokerHost() + p.getBrokerUri(), p.convertToBrokerPlace(),
					HttpURLConnection.HTTP_OK);
		}

		key.setFields(fields);
		key.setPlayers(GAMES_PREFIX + gameid + PLAYERS_SUFFIX);

		HttpService.post(this.services.getDecks(), new JSONGameURI(GAMES_PREFIX + gameid), HttpURLConnection.HTTP_OK);
	}

	private void updateBoard(Board key, JSONBoard board, String gameid) {
		List<String> placeUris = new ArrayList<>();
		board.getFields().forEach(f -> placeUris.add(f.getPlace()));
		for (int i = 0; i < board.getFields().size(); i++) {

			JSONField field = board.getFields().get(i);
			int placeNum = -1;

			try {
				placeNum = Integer.parseInt(BoardHelper.getID(field.getPlace()));
			} catch (NumberFormatException e) {
				throw new InvalidPlaceIDException(Error.PLACE_ID_NUM.getMsg());
			}

			Place p = Place.values()[placeNum];
			Field f = BoardHelper.getField(key, p.getPlaceUri());
			f.setPawns(new ArrayList<>());

			for (String pawnUri : field.getPawns()) {
				Pawn pawn = BoardHelper.getPawn(key, pawnUri);
				pawn.setPlaceUri(field.getPlace());
				pawn.setMovesUri(pawnUri + MOVE_SUFFIX);
				pawn.setPosition(placeNum);
				pawn.setRollsUri(pawnUri + ROLL_SUFFIX);
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
		Board b = BoardHelper.getBoard(boards, gameid);
		Pawn p = BoardHelper.getPawn(b, pawn.getId());

		p.setMovesUri(pawn.getMove());
		p.setPawnUri(pawn.getId());
		p.setPlaceUri(pawn.getPlace());
		p.setPlayerUri(pawn.getPlayer());
		p.setPosition(pawn.getPosition());
		p.setRollsUri(pawn.getRoll());
	}

	public Pawn getCurrplayer() {
		return currPlayer;
	}

	public void setCurrplayer(Pawn currplayer) {
		this.currPlayer = currplayer;
	}
}
