package vs.aufgabe2a.boardsservice;

import static spark.Spark.*;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import vs.aufgabe1.StatusCodes;
import vs.aufgabe2a.boardsservice.exceptions.ResourceNotFoundException;
import vs.aufgabe2a.boardsservice.models.json.JSONBoard;
import vs.aufgabe2a.boardsservice.models.json.JSONGameURI;
import vs.aufgabe2a.boardsservice.models.json.JSONPawn;
import vs.aufgabe2a.boardsservice.models.json.JSONPawnList;
import vs.aufgabe2a.boardsservice.models.json.JSONPlace;
import vs.aufgabe2a.boardsservice.models.json.JSONThrowsList;

/**
 * Restschnittstelle fuer den Boardservice
 * 
 * @author jan
 *
 */
public class BoardRESTApi {

	private final String CLRF = "\r" + "\n"; // Newline
	private final BoardService boardService = new BoardService();
	private final String CONTENT_TYPE = "application/json";

	/**
	 * Konstruktor um Schnittstelle zu initialisiseren
	 */
	public BoardRESTApi() {
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

		initGetAllBoards(); // TODO: Cleaner impl
		initGetRollsOnTheBoard(); // TODO: Cleaner impl
		initGetBoardsBelongToGameId(); // TODO: Cleaner impl
		initGetPawnsBelongToBoard(); // TODO: Cleaner impl
		initGetSpecificPawn(); // TODO: Cleaner impl
		initGetPlacesOnTheBoard();
		initGetSpecificPlace();
	}
	
	// Handler-Initialisieren
	//--------------------------------------------------------------------------------------
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
		initPutCreateNewPlace();
	}
	
	/**
	 * Delete-Handler initialisieren
	 */
	private void initDELETE() {

		initDeleteBoard();
		initDeleteSpecificPawn();

	}
	
	private void initExeptions() {

		exception(JsonSyntaxException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": Invalid Json Input!");
			exception.printStackTrace();
		});
		
		exception(ResourceNotFoundException.class, (exception, request, response) -> {
			
			response.status(StatusCodes.NOT_FOUND);
			response.body(StatusCodes.NOT_FOUND + ": Resource not Found!");
			exception.printStackTrace();
		});
	}

	// --------------------------------------------------------------------------------------
	
	private void initGetSpecificPlace() {
		get("/boards/:gameid/places/:place", "application/json", (req, resp) -> {
			JSONPlace p = boardService.getSpecificPlace(req.params(":gameid"), req.params(":place"));
			return new Gson().toJson(p);
		});

	}

	private void initGetPlacesOnTheBoard() {
		get("/boards/:gameid/places", "application/json", (req, resp) -> {
			List<String> placeList = boardService.getAllPlaces(req.params(":gameid"));
			return new Gson().toJson(placeList);
		});

	}

	private void initGetSpecificPawn() {
		get("/boards/:gameid/pawns/:pawnid", "application/json", (req, resp) -> {
			JSONPawn p = boardService.getSpecificPawn(req.params(":gameid"), req.params(":pawnid"));
			return new Gson().toJson(p);
		});

	}

	private void initGetPawnsBelongToBoard() {
		get("/boards/:gameid/pawns", CONTENT_TYPE, (req, resp) -> {
			JSONPawnList pl = boardService.getPawnsOnBoard(req.params(":gameid"));
			return new Gson().toJson(pl);
		});

	}

	private void initGetBoardsBelongToGameId() {
		get("/boards/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBoard board = boardService.getBoardForGame(req.params(":gameid"));
			return new Gson().toJson(board);
		});

	}

	private void initGetRollsOnTheBoard() {
		get("/boards/:gameid/pawns/:pawnid/roll", CONTENT_TYPE, (req, resp) -> {
			JSONThrowsList throwlist = boardService.getDiceThrows(req.queryParams(":gameid"), req.queryParams(":pawnid"));
			return new Gson().toJson(throwlist);
		});

	}

	private void initGetAllBoards() {
		get("/boards", CONTENT_TYPE, (req, resp) -> {
			return new Gson().toJson(boardService.getAllBoardURIs());
		});
	}
	

	private void initPostCreateNewBoard() {
		post("/boards", CONTENT_TYPE, (req, resp) -> {
			JSONGameURI uri = new Gson().fromJson(req.body(), JSONGameURI.class);
			boardService.createNewBoard(uri);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	private void initPostRollDice() {
		post("/boards/:gameid/pawns/:pawnid/roll", CONTENT_TYPE, (req, resp) -> {
			boardService.rollDice(req.params(":gameid"), req.params(":pawmnid"));
			return StatusCodes.SUCCESS + CLRF;
		});

	}

	private void initPostMovePawn() {
		post("/boards/:gameid/pawns/:pawnid/move", CONTENT_TYPE, (req, resp) -> {
			int roll = new Gson().fromJson(req.body(), Integer.class);
			boardService.movePawn(req.params(":gameid"), req.params(":pawnid"), roll);
			return StatusCodes.SUCCESS + CLRF;

		});

	}

	private void initPostCreateNewPawn() {
		post("/boards/:gameid/pawns", CONTENT_TYPE, (req, resp) -> {
			JSONPawn pawn = new Gson().fromJson(req.body(), JSONPawn.class);
			boardService.createNewPawnOnBoard(pawn, req.params(":gameid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}
	
	/**
	 * Befuellt einen dem Board zugeordnetem Feld mit Informationen das
	 * zugeordnete Feld wird identifiziert durch die <code>gameid</code> und der
	 * <code>place</code> : Place-ID
	 */
	private void initPutCreateNewPlace() {
		put("/boards/:gameid/places/:place", "application/json", (req, resp) -> {
			JSONPlace place = new Gson().fromJson(req.body(), JSONPlace.class);
			boardService.placeANewPlaceOnTheBoard(place, req.pathInfo(), req.params(":gameid"));
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	// TODO: Fragen
	private void initPutPlaceAPawn() {
		put("/boards/:gameid/pawns/:pawnid", "application/json", (req, resp) -> {
			JSONPawn p = new Gson().fromJson(req.body(), JSONPawn.class);
			boardService.placeAPawn(req.params(":gameid"), p);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	private void initPutPlaceABoard() {
		put("/boards/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBoard board = new Gson().fromJson(req.body(), JSONBoard.class);
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
}
