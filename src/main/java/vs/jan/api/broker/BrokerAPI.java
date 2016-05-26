package vs.jan.api.broker;

import static spark.Spark.exception;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;

import vs.jan.model.StatusCodes;

public class BrokerAPI {

	private final String CLRF = "\r" + "\n"; // Newline
	private final String CONTENT_TYPE = "application/json";
	private final Gson GSON = new Gson();

	/**
	 * Konstruktor um Schnittstelle zu initialisiseren
	 */
	public BrokerAPI() {
		initPOST();
		initPUT();
		initExeptions();
	}

	// Handler-Initialisieren
	// --------------------------------------------------------------------------------------
	/**
	 * POST-Handler initialisieren
	 */
	private void initPOST() {
		
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

	private void initPUTRegisterPlace() {

		put("/brokers/:gameid/places/:placeid", CONTENT_TYPE, (req, resp) -> {
			return "";
		});
	}

	private void initPutBroker() {
		put("/broker/:gameid", CONTENT_TYPE, (req, resp) -> {

			return "";
		});
	}
	
	private void initPostVisitPlace() {
		post("/brokers/:gameid/places/:placeid/visit/:pawnid", CONTENT_TYPE, (req, resp) -> {

			return "";
		});
	}
	
	private void initPostBuyPlace() {
		post("/brokers/:gameid/places/:placeid/owner", CONTENT_TYPE, (req, resp) -> {

			return "";
		});
	}
	
	private void initPUTPayRent(){
		
		// Bank faellige Miete melden
	}
	
	private void initPOSTPayBuyedPlace(){
		
		// Bank Bezahlung des erworbenen Grundstueckes melden
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
