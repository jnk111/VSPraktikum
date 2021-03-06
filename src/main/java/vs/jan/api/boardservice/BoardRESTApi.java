package vs.jan.api.boardservice;

import static spark.Spark.*;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import vs.jan.exception.BoardNotInitiliazedException;
import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.MutexPutException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.exception.TurnMutexNotFreeException;
import vs.jan.json.boardservice.JSONBoard;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.boardservice.JSONGameURI;
import vs.jan.json.boardservice.JSONPawn;
import vs.jan.json.boardservice.JSONPawnList;
import vs.jan.json.boardservice.JSONPlace;
import vs.jan.json.boardservice.JSONThrowsList;
import vs.jan.model.StatusCodes;
import vs.jan.model.exception.PlayerHasAlreadyRolledException;
import vs.jan.services.boardservice.BoardService;

/**
 * Restschnittstelle fuer den Boardservice
 * 
 * @author jan
 *
 */
public class BoardRESTApi {

	private final String CLRF = "\r" + "\n"; // Newline
	private BoardService boardService;
	private final String CONTENT_TYPE = "application/json";
	private final Gson GSON = new Gson();

	/**
	 * Konstruktor um Schnittstelle zu initialisiseren
	 */
	public BoardRESTApi() {
		boardService = new BoardService();
		initGET();
		initPOST();
		initPUT();
		initDELETE();
		initExeptions();
	}

	/**
	 * GET-Handler initialisieren
	 */
	private void initGET() {

		initGetAllBoards();
		initGetRollsOnTheBoard();
		initGetBoardsBelongToGameId();
		initGetPawnsBelongToBoard();
		initGetSpecificPawn();
		initGetPlacesOnTheBoard();
		initGetSpecificPlace();
	}

	// Handler-Initialisieren
	// --------------------------------------------------------------------------------------
	/**
	 * POST-Handler initialisieren
	 */
	private void initPOST() {

		initPostCreateNewBoard();
		initPostCreateNewPawn();
		initPostMovePawn();
		initPostRollDice();
	}

	/**
	 * PUT-Handler initialisieren
	 */
	private void initPUT() {
		initPutPlaceABoard();
		initPutPlaceAPawn();
		initPutUpdateAPlace();
	}

	/**
	 * Delete-Handler initialisieren
	 */
	private void initDELETE() {

		initDeleteBoard();
		initDeleteSpecificPawn();

	}

	/**
	 * Exception-Handling initialsisieren Hier werden Exceptions gefangen und ein
	 * geeignete Fehlermeldung ausgegeben
	 */
	private void initExeptions() {

		exception(JsonSyntaxException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": invalid json input");
			exception.printStackTrace();
		});

		exception(ResourceNotFoundException.class, (exception, request, response) -> {

			response.status(StatusCodes.NOT_FOUND);
			response.body(StatusCodes.NOT_FOUND + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(InvalidInputException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(MutexPutException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(TurnMutexNotFreeException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(ConnectionRefusedException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(BoardNotInitiliazedException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});
		
		exception(PlayerHasAlreadyRolledException.class, (exception, request, response) -> {

			response.status(StatusCodes.CONFLICT);
			response.body(StatusCodes.CONFLICT + ": " + exception.getMessage());
			exception.printStackTrace();
		});
	}

	// --------------------------------------------------------------------------------------

	/**
	 * Gibt ein bestimmtes Feld zurueck. Board wird identifiziert durch
	 * <code>gameid</code> Place durch <code>place</code>
	 */
	private void initGetSpecificPlace() {
		get("/boards/:gameid/places/:place", "application/json", (req, resp) -> {
			JSONPlace p = boardService.getSpecificPlace(req.params(":gameid"), req.params(":place"));
			return GSON.toJson(p);
		});
	}

	/**
	 * gibt eine Liste der Place-Uris auf dem Board zurueck Board wird
	 * identifiziert durch <code>gameid</code>
	 */
	private void initGetPlacesOnTheBoard() {
		get("/boards/:gameid/places", "application/json", (req, resp) -> {
			List<String> placeList = boardService.getAllPlaces(req.params(":gameid"));
			return GSON.toJson(placeList);
		});
	}

	/**
	 * Gibt eine bestimmte Spielfigur auf dem Board im Json-Format zurueck Board
	 * wird identifiziert durch <code>gameid</code> Pawn durch <code>pawnid</code>
	 */
	private void initGetSpecificPawn() {
		get("/boards/:gameid/pawns/:pawnid", "application/json", (req, resp) -> {
			JSONPawn p = boardService.getSpecificPawn(req.params(":gameid"), req.params(":pawnid"));
			return GSON.toJson(p);
		});

	}

	/**
	 * Gibt die Pawn-Uris der auf dem Board aufgestellten Pawns zurueck
	 */
	private void initGetPawnsBelongToBoard() {
		get("/boards/:gameid/pawns", CONTENT_TYPE, (req, resp) -> {
			JSONPawnList pl = boardService.getPawnsOnBoard(req.params(":gameid"));
			return GSON.toJson(pl);
		});

	}

	/**
	 * Gibt das Board, das zu einem Spiel gehoert als JSON zurueck
	 */
	private void initGetBoardsBelongToGameId() {
		get("/boards/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBoard board = boardService.getBoardForGame(req.params(":gameid"));
			return GSON.toJson(board);
		});

	}

	/**
	 * Gibt alle bisher getaetigten Wuerfe fuer eine bestimmte Figur zurueck
	 */
	private void initGetRollsOnTheBoard() {
		get("/boards/:gameid/pawns/:pawnid/roll", CONTENT_TYPE, (req, resp) -> {
			JSONThrowsList throwlist = boardService.getDiceThrows(req.params(":gameid"), req.params(":pawnid"));
			return GSON.toJson(throwlist);
		});

	}

	/**
	 * Gibt alle Board-Uris, die dem Spiel zugeteilt sind, zurueck
	 */
	private void initGetAllBoards() {
		get("/boards", CONTENT_TYPE, (req, resp) -> {
			return GSON.toJson(boardService.getAllBoardURIs());
		});
	}

	/**
	 * Meldet beim Spiel ein neues Board an und generiert eine URI fuer das Board.
	 * Dieses kann danach ueber HTTP-PUT mit Informtionen befuellt werden.
	 */
	private void initPostCreateNewBoard() {
		post("/boards", CONTENT_TYPE, (req, resp) -> {
			JSONGameURI uri = GSON.fromJson(req.body(), JSONGameURI.class);
			boardService.createNewBoard(uri, req.ip() + ":" + req.port());
			return StatusCodes.CREATED + CLRF;
		});
	}

	/**
	 * Laesst einen Spieler fuer seine Pawn wuerfeln, muss den Mutex fuer das
	 * Board erworben haben
	 */
	private void initPostRollDice() {
		post("/boards/:gameid/pawns/:pawnid/roll", CONTENT_TYPE, (req, resp) -> {
			JSONEventList list = boardService.rollDice(req.params(":gameid"), req.params(":pawnid"));
			return GSON.toJson(list);
		});

	}

	/**
	 * Bewegt die Spielfigur um den gegebenen Zahlenwert nach vorn.
	 */
	private void initPostMovePawn() {
		post("/boards/:gameid/pawns/:pawnid/move", CONTENT_TYPE, (req, resp) -> {
			int roll = GSON.fromJson(req.body(), Integer.class);
			boardService.movePawn(req.params(":gameid"), req.params(":pawnid"), roll);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Stellt eine neue Spielfigur auf. Startfeld ist 0 -> 'Los'
	 */
	private void initPostCreateNewPawn() {
		post("/boards/:gameid/pawns", CONTENT_TYPE, (req, resp) -> {
			JSONPawn pawn = GSON.fromJson(req.body(), JSONPawn.class);
			boardService.createNewPawnOnBoard(pawn, req.params(":gameid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Befuellt einen dem Board zugeordnetem Feld mit Informationen das
	 * zugeordnete Feld wird identifiziert durch die <code>gameid</code> und der
	 * <code>place</code> : Place-ID
	 */
	private void initPutUpdateAPlace() {
		put("/boards/:gameid/places/:place", CONTENT_TYPE, (req, resp) -> {
			JSONPlace place = GSON.fromJson(req.body(), JSONPlace.class);
			boardService.updateAPlaceOnTheBoard(place, req.pathInfo(), req.params(":gameid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * TODO: Fragen Weißt einer Figur ein neues Feld zu, z. B. nach wuerfeln und
	 * weiterbewegen
	 */
	private void initPutPlaceAPawn() {
		put("/boards/:gameid/pawns/:pawnid", CONTENT_TYPE, (req, resp) -> {
			JSONPawn p = GSON.fromJson(req.body(), JSONPawn.class);
			boardService.placeAPawn(req.params(":gameid"), p);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Befuellt ein dem Game zugeteiltes Board mit Informationen z. B.: Feldern
	 */
	private void initPutPlaceABoard() {
		put("/boards/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBoard board = GSON.fromJson(req.body(), JSONBoard.class);
			boardService.placeABoard(req.params(":gameid"), board);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Loescht eine bestimmte Spielfigur aus dem Spiel. Diese wird identfiziert
	 * anhand der <code>gameid</code> und der <code>pawnid</code>
	 * 
	 */
	private void initDeleteSpecificPawn() {
		delete("/boards/:gameid/pawns/:pawnid", CONTENT_TYPE, (req, resp) -> {
			boardService.deletePawnFromBoard(req.params(":gameid"), req.params(":pawnid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	/**
	 * Loescht ein Board aus einem Spiel und beendet damit das Spiel Das Board
	 * wird identifiziert anhand der <code>gameid</code>
	 */
	private void initDeleteBoard() {
		delete("/boards/:gameid", CONTENT_TYPE, (req, resp) -> {
			boardService.deleteBoard(req.params(":gameid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	public BoardService getBoardService() {
		return boardService;
	}
}
