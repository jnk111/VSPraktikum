package vs.jan.api.broker;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;

import vs.jan.json.brokerservice.JSONBroker;
import vs.jan.json.brokerservice.JSONBrokerList;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.json.brokerservice.JSONPlace;
import vs.jan.model.StatusCodes;
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
		initExeptions();
	}

	private void initGET() {
		initGETBrokers();
		
	}

	// Handler-Initialisieren
	// --------------------------------------------------------------------------------------
	
	private void initGETBrokers() {
		get("/brokers", "application/json", (req, resp) -> {
			
			JSONBrokerList list = service.getBrokers();
			return GSON.toJson(list);
		});
		
	}
	
	/**
	 * POST-Handler initialisieren
	 */
	private void initPOST() {
		
		initPOSTCreateBroker();
		initPostBuyPlace();
		initPOSTPayBuyedPlace();
		initPostVisitPlace();
	}
	
	private void initPUT() {
		initPutBroker();
		initPUTRegisterPlace();
		initPUTPayRent();
		initPUTRegisterPlace();
	}


	private void initPOSTCreateBroker() {
		post("/brokers", CONTENT_TYPE, (req, resp) -> {
			service.createBroker(GSON.fromJson(req.body(), JSONGameURI.class), req.host());
			return StatusCodes.SUCCESS + CLRF;
		});
	}
	
	private void initPostVisitPlace() {
		post("/brokers/:gameid/places/:placeid/visit/:pawnid", CONTENT_TYPE, (req, resp) -> {

			return "";
		});
	}
	
	private void initPOSTPayBuyedPlace(){
		
		// Bank Bezahlung des erworbenen Grundstueckes melden
	}
	
	private void initPostBuyPlace() {
		post("/brokers/:gameid/places/:placeid/owner", CONTENT_TYPE, (req, resp) -> {

			return "";
		});
	}

	private void initPUTRegisterPlace() {

		put("/brokers/:gameid/places/:placeid", CONTENT_TYPE, (req, resp) -> {
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
	
	
	private void initPUTPayRent(){
		
		// Bank faellige Miete melden
	}



	/**
	 * Exception-Handling initialsisieren Hier werden Exceptions gefangen und ein
	 * geeignete Fehlermeldung ausgegeben
	 */
	private void initExeptions() {

		exception(Exception.class, (exception, request, response) -> {

			response.status(StatusCodes.BAD_REQ);
			response.body(StatusCodes.BAD_REQ + ": ");
			exception.printStackTrace();
		});

	}
}
