package vs.jan.api.broker;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.net.HttpURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import vs.jan.exception.ConnectionRefusedException;
import vs.jan.exception.InvalidInputException;
import vs.jan.exception.NotImplementedException;
import vs.jan.exception.ResourceNotFoundException;
import vs.jan.json.boardservice.JSONEventList;
import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONEstates;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.StatusCodes;
import vs.jan.model.boardservice.Player;
import vs.jan.model.exception.PlaceNotHasAnOwnerException;
import vs.jan.model.exception.TransactionFailedException;
import vs.jan.model.exception.TransactionRollBackException;
import vs.jan.services.broker.BrokerService;

public class BrokerAPI {

	private final String CLRF = "\r" + "\n"; // Newline
	private final String CONTENT_TYPE = "application/json";
	private final Gson GSON = new Gson();
	private BrokerService service = new BrokerService();

	/**
	 * Konstruktor um Schnittstelle zu initialisiseren
	 */
	public BrokerAPI() {
		initGET();
		initPOST();
		initPUT();
		initDELETE();
		initExeptions();
	}

	private void initGET() {
		initGETBrokers();
		initGETSpecificBroker();
		initGETPlaces();
		initGETSpecificPlace();
		initGetOwner();
	}

	/**
	 * POST-Handler initialisieren
	 */
	private void initPOST() {

		initPOSTCreateBroker();
		initPostBuyPlace();
		initPostVisitPlace();
		initPostTradeRequest();
	}

	private void initPUT() {
		initPutBroker();
		initPUTRegisterPlace();
		initPUTTradePlace();
		initPUTTakeHypothecaryCredit();
	}

	private void initDELETE() {
		initDELETERemoveHypothecaryCredit();
	}

	private void initDELETERemoveHypothecaryCredit() {
		delete("/broker/:gameid/places/:placeid/hypothecarycredit", CONTENT_TYPE, (req, resp) -> {
			String playerUri = GSON.fromJson(req.body(), String.class);
			JSONEventList list = service.deleteHypothecaryCredit(req.params(":gameid"), req.params(":placeid"), playerUri,
					req.pathInfo());
			return GSON.toJson(list);
		});
	}

	private void initPUTTakeHypothecaryCredit() {
		put("/broker/:gameid/places/:placeid/hypothecarycredit", CONTENT_TYPE, (req, resp) -> {
			String playerUri = GSON.fromJson(req.body(), String.class);
			JSONEventList list = service.takeHypothecaryCredit(req.params(":gameid"), req.params(":placeid"), playerUri,
					req.pathInfo());
			return GSON.toJson(list);
		});

	}

	private void initPUTTradePlace() {
		put("/broker/:gameid/places/:placeid/owner", CONTENT_TYPE, (req, resp) -> {

			Player player = GSON.fromJson(req.body(), Player.class);
			JSONEventList list = service.tradePlace(req.params(":gameid"), req.params(":placeid"), player, req.pathInfo());
			return GSON.toJson(list);
		});
	}

	// Handler-Initialisieren
	// --------------------------------------------------------------------------------------

	private void initGetOwner() {
		get(" /broker/:gameid/places/:placeid/owner ", CONTENT_TYPE, (req, resp) -> {
			Player owner = service.getOwner(req.params(":gameid"), req.params(":placeid"));
			return GSON.toJson(owner);
		});

	}

	private void initGETBrokers() {
		get("/broker", CONTENT_TYPE, (req, resp) -> {
			JSONBrokerList list = service.getBrokers();
			return GSON.toJson(list);
		});
	}

	private void initGETSpecificBroker() {
		get("/broker/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBroker broker = service.getSpecificBroker(req.params(":gameid"));
			return GSON.toJson(broker);
		});
	}

	private void initGETPlaces() {
		get("/broker/:gameid/places", CONTENT_TYPE, (req, resp) -> {
			JSONEstates estates = service.getPlaces(req.params(":gameid"));
			return GSON.toJson(estates);
		});
	}

	private void initGETSpecificPlace() {
		get("/broker/:gameid/places/:placeid", CONTENT_TYPE, (req, resp) -> {
			JSONPlace place = service.getSpecificPlace(req.params(":gameid"), req.params(":placeid"));
			return GSON.toJson(place);
		});
	}

	private void initPOSTCreateBroker() {
		post("/broker", CONTENT_TYPE, (req, resp) -> {
			JSONGameURI gameUri = GSON.fromJson(req.body(), JSONGameURI.class);
			String serviceUri = req.queryParams("services");
			service.createBroker(gameUri, serviceUri);
			return StatusCodes.SUCCESS + CLRF;
		});
	}

	private void initPostVisitPlace() {
		post("/broker/:gameid/places/:placeid/visit/:pawnid", CONTENT_TYPE, (req, resp) -> {
			String playerUri = GSON.fromJson(req.body(), String.class);
			JSONEventList list = service.visitPlace(req.params(":gameid"), req.params(":placeid"), req.params(":pawnid"),
					playerUri, req.pathInfo());
			return GSON.toJson(list);
		});
	}

	private void initPostBuyPlace() {
		post("/broker/:gameid/places/:placeid/owner", CONTENT_TYPE, (req, resp) -> {
			String playerUri = GSON.fromJson(req.body(), String.class);
			JSONEventList list = service.buyPlace(req.params(":gameid"), req.params(":placeid"), playerUri, req.pathInfo());
			return GSON.toJson(list);
		});
	}

	private void initPostTradeRequest() {
		post("/broker/:gameid/places/:placeid/trade/:pawnid", CONTENT_TYPE, (req, resp) -> {
			service.tradeRequest(req.params(":gameid"), req.params(":placeid"), req.params(":pawnid"), req.pathInfo());
			return StatusCodes.CREATED + CLRF;
		});

	}

	private void initPUTRegisterPlace() {

		put("/broker/:gameid/places/:placeid", CONTENT_TYPE, (req, resp) -> {
			JSONPlace place = GSON.fromJson(req.body(), JSONPlace.class);
			service.registerPlace(req.params(":gameid"), req.params(":placeid"), place);
			return StatusCodes.CREATED + CLRF;
		});
	}

	private void initPutBroker() {
		put("/broker/:gameid", CONTENT_TYPE, (req, resp) -> {
			JSONBroker broker = GSON.fromJson(req.body(), JSONBroker.class);
			service.placeBroker(req.params(":gameid"), broker);
			return StatusCodes.SUCCESS + CLRF;
		});
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

		exception(ConnectionRefusedException.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(NotImplementedException.class, (exception, request, response) -> {

			response.status(HttpURLConnection.HTTP_BAD_METHOD);
			response.body(HttpURLConnection.HTTP_BAD_METHOD + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(TransactionFailedException.class, (exception, request, response) -> {

			response.status(HttpURLConnection.HTTP_CONFLICT);
			response.body(HttpURLConnection.HTTP_CONFLICT + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(PlaceNotHasAnOwnerException.class, (exception, request, response) -> {

			response.status(HttpURLConnection.HTTP_NOT_FOUND);
			response.body(HttpURLConnection.HTTP_NOT_FOUND + ": " + exception.getMessage());
			exception.printStackTrace();
		});

		exception(TransactionRollBackException.class, (exception, request, response) -> {

			response.status(HttpURLConnection.HTTP_CONFLICT);
			response.body(HttpURLConnection.HTTP_CONFLICT + ": " + exception.getMessage());
			exception.printStackTrace();
		});
	}
}
